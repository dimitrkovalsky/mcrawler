package com.liberty.crawler;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: Dimitr
 * Date: 21.05.2017
 * Time: 9:48
 */
@Slf4j
@Component
public class FlickrCrawler {

//    @Autowired
//    private ArtistImageRepository imageRepository;
//
//    private static final String SEARCH_URL = "https://www.flickr.com/search/?text=%s";
//
//    public int crawlPhoto(String artistName, ObjectId id) {
//        String url = String.format(SEARCH_URL, URLEncoder.encode(artistName));
//        String result = RequestHelper.executeRequestAndGetResult(url);
//        ArtistImage one = imageRepository.findOne(id);
//        if (one == null) {
//            one = new ArtistImage(id, new HashSet<>());
//        }
//        Set<String> urls = getSmallImages(result);
//        one.getSmallImages().addAll(urls);
//        imageRepository.save(one);
//        return urls.size();
//    }
//
//    private Set<String> getSmallImages(String result) {
//        String[] split = result.split("img.src='//");
//        return Arrays.stream(split).filter(s -> s.contains("staticflickr")).map(str -> {
//            int endIndex = str.indexOf(".jpg");
//            return str.substring(0, endIndex + 4);
//        }).collect(Collectors.toSet());
//    }
}
