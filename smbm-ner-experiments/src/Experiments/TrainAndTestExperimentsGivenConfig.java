package Experiments;

import java.util.Vector;

import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import LBJ2.classify.Classifier;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import LbjTagger.Data;
import LbjTagger.Parameters;
import LbjTagger.ParametersForLbjCode;

public class TrainAndTestExperimentsGivenConfig {
	public static Vector<Data> readOutOfDomainTestData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		//Data data=new Data("../Data/GoldData/Arts/godby.columns.gold","art", "-c",new String[]{},new String[]{});
		Data data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.training.sentences.columns.gold", "muc7train","-c",new String[]{"MISC"},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.dryrun.sentences.columns.gold", "muc7dry","-c",new String[]{"MISC"},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.formalrun.sentences.columns.gold","muc7formal", "-c",new String[]{"MISC"},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		data=new Data("../Data/GoldData/WebpagesColumns/","web", "-c",new String[]{},new String[]{"MISC","PER","LOC","ORG"});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;
	}
	public static Vector<Data> readCoNLLTestData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test", "conll03test","-c",new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;		
	}
	public static Vector<Data> readCoNLLDevData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev", "conll03dev","-c",new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;		
	}
	public static Vector<Data> readCoNLLTrainData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train","conl03train", "-c",new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;		
	}

	public static void main(String[] args) throws Exception{
		String configFile=args[0];
		Parameters.readConfigAndLoadExternalData(configFile);
		Vector<Data> inDomainTest=readCoNLLTestData();
		Vector<Data> inDomainDev=readCoNLLDevData();
		Vector<Data> inDomainTrain=readCoNLLTrainData();
		Vector<Data> outOfDomainData=readOutOfDomainTestData();
		//tuning on the in-domain data (CoNLL test)
		String originalModelPath=ParametersForLbjCode.pathToModelFile;
		String originalLogFile=ParametersForLbjCode.debuggingLogPath;
		
		ParametersForLbjCode.pathToModelFile=originalModelPath+".CoNLLTestTuning";
		ParametersForLbjCode.debuggingLogPath=originalLogFile+".CoNLLTestTuning";
		System.out.println("-------------------------------------------------------------");
		System.out.println("------Training with tuning on CoNLL test dataset       ------");
		System.out.println("-------------------------------------------------------------");
		LbjTagger.LearningCurveMultiDataset.getLearningCurve(inDomainTrain,inDomainTest);
		System.out.println("-------------------------------------------------------------");
		System.out.println("------PERFORMANCE WHEN TUNING ON THE CONLL TEST SET    ------");
		System.out.println("-------------------------------------------------------------");
		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainTest,taggerLevel1,taggerLevel2,true);	
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainDev,taggerLevel1,taggerLevel2,true);
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(outOfDomainData,taggerLevel1,taggerLevel2,true);
		LbjTagger.NETesterMultiDataset.printAllTestResultsAsOneDataset(outOfDomainData,taggerLevel1,taggerLevel2,true); 


		ParametersForLbjCode.pathToModelFile=originalModelPath+".OutOfDomainTuning";
		ParametersForLbjCode.debuggingLogPath=originalLogFile+".OutOfDomainTuning";
		System.out.println("-------------------------------------------------------------");
		System.out.println("------Training with tuning on out of domain dataset    ------");
		System.out.println("-------------------------------------------------------------");
		LbjTagger.LearningCurveMultiDataset.getLearningCurve(inDomainTrain,outOfDomainData);
		System.out.println("-------------------------------------------------------------");
		System.out.println("----PERFORMANCE WHEN TUNING ON THE OUT OF DOMAIN DATA  ------");
		System.out.println("-------------------------------------------------------------");
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainTest,taggerLevel1,taggerLevel2,true);	
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainDev,taggerLevel1,taggerLevel2,true);
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(outOfDomainData,taggerLevel1,taggerLevel2,true);
		LbjTagger.NETesterMultiDataset.printAllTestResultsAsOneDataset(outOfDomainData,taggerLevel1,taggerLevel2,true); 

	
		ParametersForLbjCode.pathToModelFile=originalModelPath+".CoNLLDevTuning";
		ParametersForLbjCode.debuggingLogPath=originalLogFile+".CoNLLDevTuning";
		System.out.println("-------------------------------------------------------------");
		System.out.println("------Training with tuning on the CoNLL Dev dataset    ------");
		System.out.println("-------------------------------------------------------------");
		LbjTagger.LearningCurveMultiDataset.getLearningCurve(inDomainTrain,inDomainDev);
		System.out.println("-------------------------------------------------------------");
		System.out.println("----PERFORMANCE WHEN TUNING ON THE CoNLL Dev DATA  ------");
		System.out.println("-------------------------------------------------------------");
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainTest,taggerLevel1,taggerLevel2,true);	
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainDev,taggerLevel1,taggerLevel2,true);
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(outOfDomainData,taggerLevel1,taggerLevel2,true);
		LbjTagger.NETesterMultiDataset.printAllTestResultsAsOneDataset(outOfDomainData,taggerLevel1,taggerLevel2,true); 	
	}
}
