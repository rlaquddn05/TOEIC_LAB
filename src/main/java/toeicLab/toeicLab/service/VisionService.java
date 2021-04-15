package toeicLab.toeicLab.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisionService {

    /**
     * 사용자가 이미지로 업로드한 파일의 내용을 google vision api를 통해 텍스트로 추출한다.
     * @param fileName
     * @param loadText
     */
    public void readText(String fileName, StringBuilder loadText){
        try {
            String resource = System.getProperty("java.io.tmpdir") + fileName + ".jpg";

            List<AnnotateImageRequest> requests = new ArrayList<>();
            ByteString imgBytes = ByteString.readFrom(new FileInputStream(resource));

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                List<AnnotateImageResponse> responses = response.getResponsesList();

                for (AnnotateImageResponse res : responses) {
                    if (res.hasError()) {
                        return;
                    }
                    loadText.append(res.getTextAnnotationsList().get(0).getDescription());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
