package com.smh.util;

import com.google.cloud.language.v1beta2.*;
import com.smh.cs.model.NLAnalyzeVO;

import java.util.List;

public class NLAnalyze {

	private static NLAnalyze instance = new NLAnalyze();
	private static LanguageServiceClient languageServiceClient;

	public static NLAnalyze getInstance() {
		try {
			languageServiceClient = LanguageServiceClient.create();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return instance;
	}

	public NLAnalyzeVO analyze(String text) {

		NLAnalyzeVO nlAnalyzeVo = new NLAnalyzeVO();

		List<Token> tokenList = analyzeSyntax(text);

		for (Token token : tokenList) {
			String tag = token.getPartOfSpeech().getTag().toString();
			String content = token.getText().getContent();

			if (tag.equals("NOUN"))
				nlAnalyzeVo.setNouns(content);
			else if (tag.equals("ADJ"))
				nlAnalyzeVo.setAdjs(content);
		}

		return nlAnalyzeVo;
	}

	public List<Entity> analyzeEntities(String text) {
		try {
			Document document = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();

			AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder().setDocument(document)
					.setEncodingType(EncodingType.UTF16).build();

			AnalyzeEntitiesResponse response = languageServiceClient.analyzeEntities(request);

			return response.getEntitiesList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public Sentiment analyzeSentiment(String text) {
		try {
			Document document = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();

			AnalyzeSentimentResponse response = languageServiceClient.analyzeSentiment(document);
			return response.getDocumentSentiment();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<Token> analyzeSyntax(String text) {
		try {
			Document document = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();

			AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder().setDocument(document)
					.setEncodingType(EncodingType.UTF16).build();

			AnalyzeSyntaxResponse response = languageServiceClient.analyzeSyntax(request);

			return response.getTokensList();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public List<ClassificationCategory> analyzeCategories(String text) {
		try {
			Document document = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();

			ClassifyTextRequest request = ClassifyTextRequest.newBuilder().setDocument(document).build();

			ClassifyTextResponse response = languageServiceClient.classifyText(request);
			
			return response.getCategoriesList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}