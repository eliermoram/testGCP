package com.gcp.demo.adapter;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import com.google.cloud.storage.BlobInfo;
import java.io.IOException;
import java.nio.file.Files;


@Component
public class StorageClientAdapter {

	public static void downloadObject(String projectId, String bucketName, String objectName, String destFilePath) {

		Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

		Blob blob = storage.get(BlobId.of(bucketName, objectName));
		blob.downloadTo(Paths.get(destFilePath));

	}

	public static void uploadObject(String projectId, String bucketName, String objectName, String filePath)
			throws IOException {

		Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
		BlobId blobId = BlobId.of(bucketName, objectName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

	}

}
