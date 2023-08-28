package com.pdebray.hashing;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App {

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {

		Transcript transcript = new Transcript();
		transcript.setAudio_url(Constants.URL_REPO);

		Gson gson = new Gson();
		String jsonRequest = gson.toJson(transcript);

		HttpRequest postRequest = (HttpRequest) HttpRequest.newBuilder()
				.uri(new URI(Constants.ENDPOINT_POST)).header("Authorization", Constants.API_KEY)
				.POST(BodyPublishers.ofString(jsonRequest)).build();

		HttpClient httpClient = HttpClient.newHttpClient();

		HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());

		System.out.println(postResponse.body());

		logger.info("postRequest is done with json: {}", jsonRequest);

		transcript = gson.fromJson(postResponse.body(), Transcript.class);
		
		

		HttpRequest getRequest = (HttpRequest) HttpRequest.newBuilder()
				.uri(new URI(Constants.ENDPOINT_GET + transcript.getId()))
				.header("Authorization", Constants.API_KEY).build();

		while (true) {
			HttpResponse<String> getResponse = httpClient.send(getRequest, BodyHandlers.ofString());
			transcript = gson.fromJson(getResponse.body(), Transcript.class);

			System.out.println(transcript.getStatus());

			if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
				break;
			}
			Thread.sleep(1000);
		}

		logger.info("transcription done with status: {}", transcript.getStatus());
		System.out.println(transcript.getText());

	}

}