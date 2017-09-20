package com.liberty.service;

import com.liberty.model.GenericTrack;
import com.liberty.model.MbArtist;
import com.liberty.model.StreamTrack;
import com.liberty.repository.GenericTrackRepository;
import com.liberty.repository.MbArtistRepository;
import com.liberty.soundcloud.model.SoundCloudTrack;
import com.liberty.soundcloud.repo.SoundCloudRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.liberty.common.StringHelper.cleanString;
import static java.util.function.Function.identity;

@Service
@Slf4j
public class SoundCloudLinker {
    @Autowired
    private SoundCloudRepository soundCloudRepository;
    @Autowired
    private GenericTrackRepository genericTrackRepository;
    @Autowired
    private MbArtistRepository mbArtistRepository;


    public void linkSoundCloud() {
        //        Map<String, List<SoundCloudTrack>> scTracks = loadScTracks();
        Map<String, List<GenericTrack>> genericTracks = loadGenericTracks();
        //        log.info("Loaded {} artist from musicbrainz and {} artist from soundcloud", genericTracks.size(), scTracks.size());

        AtomicInteger counter = new AtomicInteger(0);
        genericTracks.forEach((artistName, tracks) -> {
            log.info("Starting processing for {} artist", artistName);
            List<SoundCloudTrack> soundCloudTracks = findSoundCloudTracks(artistName);
            if (!CollectionUtils.isEmpty(soundCloudTracks)) {
                tracks.parallelStream().forEach(genericTrack -> {
                    List<StreamTrack> streams = getSoundCloudStreams(soundCloudTracks, genericTrack);
                    genericTrack.addStreams(streams);
                    genericTrackRepository.save(genericTrack);
                    int added = 0;
                    if (!CollectionUtils.isEmpty(streams)) {
                        added = streams.size();
                    }
                    log.info("Updated {} track with {} streams from SoundCloud", genericTrack.getName(), added);
                });
            }

            log.info("Completed processing for {} artist", artistName);
            log.info("Processed tracks {}/{}", counter.incrementAndGet(), genericTracks.size());

        });
    }

    private List<SoundCloudTrack> findSoundCloudTracks(String artistName) {
        List<SoundCloudTrack> byArtistName = soundCloudRepository.findByArtistName(artistName);
        int size = 0;
        if (byArtistName != null)
            size = byArtistName.size();
        log.info("Found {} track in SoundCloud database for {} artist", size, artistName);
        return byArtistName;
    }

    private List<StreamTrack> getSoundCloudStreams(List<SoundCloudTrack> soundCloudTracks, GenericTrack genericTrack) {
        return soundCloudTracks.stream().filter(t -> {
            String cleanName = cleanString(t.getTrackName());
            return cleanName.contains(cleanString(genericTrack.getName()));
        }).map(SoundCloudTrack::toStreamTrack).collect(Collectors.toList());
    }


    private Map<String, List<GenericTrack>> loadGenericTracks() {
        Map<String, List<GenericTrack>> gnTracks = new HashMap<>();
        Map<String, MbArtist> mbArtistMap = mbArtistRepository.findAll().stream()
                .collect(Collectors.toMap(MbArtist::getMbid, identity()));
        int page = 0;
        int size = 1000;
        boolean completed = false;
        AtomicInteger counter = new AtomicInteger(page * size);
        long total = genericTrackRepository.count();
        while (!completed) {
            List<GenericTrack> all = genericTrackRepository.findAll(new PageRequest(page, size)).getContent();
            for (GenericTrack gTrack : all) {
                String mbid = gTrack.getArtistMbib();
                MbArtist mbArtist = mbArtistMap.get(mbid);
                if (mbArtist == null) {
                    log.error("Can not find artist for mbid: {}", mbid);
                    continue;
                }
                String artistName = cleanString(mbArtist.getName());
                gnTracks.computeIfAbsent(artistName, k -> new ArrayList<>());
                gnTracks.get(artistName).add(gTrack);
            }
            System.out
                    .println(String.format("Loaded %s / %s musicbrainz tracks", counter.addAndGet(all.size()), total));
            page++;
            if (all.size() < size) {
                completed = true;
            }
        }
        return gnTracks;
    }


//    private Map<String, List<SoundCloudTrack>> loadScTracks() {
//        Map<String, List<SoundCloudTrack>> scTracks = new HashMap<>();
//        int page = 0;
//        int size = 50000;
//        boolean completed = false;
//        AtomicInteger counter = new AtomicInteger(page * size);
//        long total = soundCloudRepository.count();
//        while (!completed) {
//            List<SoundCloudTrack> all = soundCloudRepository.findAll(new PageRequest(page, size)).getContent();
//            all.forEach(scTrack -> {
//                parseNames(scTrack);
//                String artistName = scTrack.getArtistName();
//                scTracks.computeIfAbsent(artistName, k -> new ArrayList<>());
//                scTracks.get(artistName).add(scTrack);
//            });
//            System.out
//                    .println(String.format("Loaded %s / %s sound cloud tracks", counter.addAndGet(all.size()), total));
//            page++;
//            if (all.size() < size) {
//                completed = true;
//            }
//        }
//        return scTracks;
//    }
//
//    private void parseNames(SoundCloudTrack scTrack) {
////        String name = scTrack.getName();
////        int start = name.indexOf("-");
////        String artistName = cleanString(name.substring(0, start));
////        String trackName = cleanString(name.substring(start + 1));
////        scTrack.setArtistName(artistName);
////        scTrack.setTrackName(trackName);
//    }

}
