package DNBN.spring.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/default-images")
public class DefaultImageController {
    private static final String S3_BASE_URL = "https://dnbn-bucket.s3.ap-northeast-2.amazonaws.com/default-images/";
    private static final String[] UUIDS = {
        "ae383fd6-4211-496b-a631-827954b03306",
        "b0fcf0f5-ddde-4392-8820-7336ab791c0a",
        "0afdd25f-a73c-497a-9982-e15fcfc0e45d",
        "62b36a8f-44df-431c-94f6-e40d2e18593a",
        "bb686590-d938-4e89-a4f3-baa3a1e9855c",
        "24fe7798-7e86-47e3-881d-8296df20ba6a",
        "70cf8d89-197f-40c8-a897-0f93530d5512",
        "571d8849-eabb-4ec3-b63f-531e4ad268a8",
        "bf07c342-d591-4fe7-b1ea-9bcf65243906",
        "4db5c789-f20d-4efd-b857-d6423abd8332",
        "5ace125c-e513-4f55-aab5-63bafbd3ac87",
        "4c44b391-a793-4665-a749-2b780d11d1ea",
        "9a7386b7-a41c-4d6e-b473-7a0827bfa1e6",
        "65e8e438-ef5a-47d9-ac01-e1df7d3a9490",
        "36733831-e6fd-49e6-a700-b6d5018e1620",
        "5a7505e3-a344-495b-880b-7b214853f590",
        "0eded488-11ac-41ba-83cd-5a0a96a62ec6",
        "775e23fc-66ad-4f19-8fae-a0298de02b62",
        "06efb6fc-d447-4635-8f3b-b93aceba3a6b",
        "fa6d1d50-3810-43d9-921e-0d22dfd677dc",
        "af1ca572-0298-4931-908d-3362d588bc7a"
    };

    @GetMapping
    public List<String> getDefaultImageUrls() {
        return java.util.Arrays.stream(UUIDS)
                .map(uuid -> S3_BASE_URL + uuid)
                .collect(Collectors.toList());
    }
}
