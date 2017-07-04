package com.liberty.service;

import com.liberty.jpa.ArtistRepository;
import com.liberty.jpa.MediumRepository;
import com.liberty.jpa.ReleaseRepository;
import com.liberty.model.GenericTrack;
import com.liberty.model.MbArtist;
import com.liberty.model.MbTrack;
import com.liberty.model.PleerTrack;
import com.liberty.model.StreamTrack;
import com.liberty.model.ZaycevTrack;
import com.liberty.repository.GenericTrackRepository;
import com.liberty.repository.MbAlbumRepository;
import com.liberty.repository.MbArtistRepository;
import com.liberty.repository.MbTrackRepository;
import com.liberty.repository.PleerArtistRepository;
import com.liberty.repository.PleerTrackRepository;
import com.liberty.repository.ZaycevTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * Created by dkovalskyi on 03.07.2017.
 */
@Component
public class TrackLinker {
    @Autowired
    private MbArtistRepository mbArtistRepository;
    @Autowired
    private MbTrackRepository mbTrackRepository;
    @Autowired
    private GenericTrackRepository genericTrackRepository;
    @Autowired
    private ZaycevTrackRepository zaycevTrackRepository;
    @Autowired
    private PleerTrackRepository pleerTrackRepository;
    @Autowired
    private PleerArtistRepository pleerArtistRepository;

    private String cleanString(String toClean) {
        if (StringUtils.isEmpty(toClean)) {
            return "";
        }
        return toClean.toLowerCase().replaceAll("[^\\p{L}\\p{Z}]", "");
    }

    private List<StreamTrack> getPleerStreams(MbTrack track, MbArtist artist) {
        List<PleerTrack> tracks = pleerTrackRepository.findByArtistName(artist.getName().toLowerCase());
        if (tracks == null) {
            System.err.println("Not found pleer tracks for : " + artist.getName());
            return emptyList();
        }
        System.out.println(String.format("Found %s track for %s artist in Pleer database", tracks.size(), artist.getName()));

        return tracks.stream().filter(t -> {
            String cleanName = cleanString(t.getSong());
            return cleanName.contains(cleanString(track.getName()));
        }).map(PleerTrack::toStreamTrack).collect(Collectors.toList());
    }

    private List<StreamTrack> getZaycevStreams(MbTrack track, MbArtist artist) {
        List<ZaycevTrack> tracks = zaycevTrackRepository.findByArtistName(artist.getName().toLowerCase());
        if (tracks == null) {
            System.err.println("Not found zaycev tracks for : " + artist.getName());
            return emptyList();
        }
        System.out.println(String.format("Found %s track for %s artist in Zaycev database", tracks.size(), artist.getName()));

        return tracks.stream().filter(t -> {
            String cleanName = cleanString(t.getTrackName());
            return cleanName.contains(cleanString(track.getName()));
        }).map(ZaycevTrack::toStreamTrack).collect(Collectors.toList());
    }

    public void linkTracks() {
        int page = 0;
        int size = 1000;
        boolean completed = false;
        AtomicInteger counter = new AtomicInteger(page * size);
        long total = mbTrackRepository.count();
        while (!completed) {
            List<MbTrack> all = mbTrackRepository.findAll(new PageRequest(page, size)).getContent();
            all.parallelStream().forEach(t -> {
                linkTracks(t.getMbid());
                System.out.println(String.format("Processed %s / %s tracks", counter.incrementAndGet(), total));
            });
            page++;
            if (all.size() < size) {
                completed = true;
            }
        }
    }

    private void linkTracks(String trackMbid) {
        MbTrack track = mbTrackRepository.findOne(trackMbid);
        MbArtist artist = mbArtistRepository.findOne(track.getArtistMbib());

        List<StreamTrack> pleerStreams = getPleerStreams(track, artist);
        List<StreamTrack> zaycevStreams = getZaycevStreams(track, artist);
        GenericTrack genericTrack = new GenericTrack(track);

        genericTrack.addStreams(pleerStreams);
        genericTrack.addStreams(zaycevStreams);
        System.out.println(String.format("Found %s streams for track: %s and artist: %s", genericTrack.getStreams().size(), track.getName(), artist.getName()));
        genericTrackRepository.save(genericTrack);
    }

}
