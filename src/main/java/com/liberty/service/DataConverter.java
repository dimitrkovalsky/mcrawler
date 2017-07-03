package com.liberty.service;

import com.liberty.entity.*;
import com.liberty.jpa.ArtistRepository;
import com.liberty.jpa.MediumRepository;
import com.liberty.jpa.ReleaseRepository;
import com.liberty.model.MbAlbum;
import com.liberty.model.MbArtist;
import com.liberty.model.MbTrack;
import com.liberty.repository.MbAlbumRepository;
import com.liberty.repository.MbArtistRepository;
import com.liberty.repository.MbTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Component
public class DataConverter {
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private MediumRepository mediumRepository;
    @Autowired
    private MbArtistRepository mbArtistRepository;
    @Autowired
    private MbTrackRepository mbTrackRepository;
    @Autowired
    private MbAlbumRepository mbAlbumRepository;
    
    public void runArtistConverter() {
        int page = 0;
        int size = 10000;
        boolean completed = false;
        AtomicInteger counter = new AtomicInteger(0);
        long total = artistRepository.count();
        while (!completed) {
            List<ArtistEntity> all = artistRepository.findAll(new PageRequest(page, size)).getContent();
            List<MbArtist> artists = all.stream().map(ArtistEntity::toMongoEntity).collect(Collectors.toList());
            mbArtistRepository.save(artists);
            System.out.println(String.format("Processed %s / %s artists", counter.addAndGet(artists.size()), total));
            page++;
            if (all.size() < size) {
                completed = true;
            }
        }
    }
    public void runAlbumConverter() {
        int page = 386;
        int size = 1000;
        boolean completed = false;
        AtomicInteger counter = new AtomicInteger(page * size);
        long total = mbArtistRepository.count();
        while (!completed) {
            List<MbArtist> all = mbArtistRepository.findAll(new PageRequest(page, size)).getContent();
            all.parallelStream().forEach(a -> {
                runAlbumConverter(a.getInternalId());
                System.out.println(String.format("Processed %s / %s artists", counter.incrementAndGet(), total));
            });
            page++;
            if (all.size() < size) {
                completed = true;
            }
        }
    }

    public void convertArtistWithAlbums(Integer internalId) {
        ArtistEntity one = artistRepository.findOne(internalId);
        MbArtist mbArtist = one.toMongoEntity();
        mbArtistRepository.save(mbArtist);
        runAlbumConverter(internalId);
    }

    private void runAlbumConverter(int artistId) {
        ArtistEntity artistEntity = artistRepository.findOne(artistId);
        List<ReleaseEntity> releases = releaseRepository.findAllByArtistId(artistId);
        releases.forEach(r -> {
            List<MediumEntity> release = mediumRepository.findAllByRelease(r.getId());
            if (release != null) {
                release.forEach(rel -> {
                    saveTracks(artistEntity, r, rel);
                    MbAlbum album = new MbAlbum();
                    album.setMbid(r.getMbid().toString());
                    album.setInternalId(r.getId());
                    album.setName(r.getName());
                    album.setArtistInternalId(artistEntity.getId());
                    album.setArtistMbib(artistEntity.getMbid().toString());
                    if (rel.getTracks() != null) {
                        album.setTags(r.getTags().stream().map(TagEntity::getName).collect(Collectors.toList()));
                    }
                    if (rel.getTracks() != null) {
                        List<MbAlbum.AlbumTrack> albumTracks = rel.getTracks().stream()
                            .map(t -> new MbAlbum.AlbumTrack(t.getMbid().toString(), t.getName()))
                            .collect(Collectors.toList());
                        album.setTracks(albumTracks);
                        System.out.println(String.format("Stored %s album with %s tracks for %s artist",
                            album.getName(), albumTracks.size(), artistEntity.getName()));
                    }
                    mbAlbumRepository.save(album);
                });
            }
        });
    }

    private List<MbTrack> saveTracks(ArtistEntity artistEntity, ReleaseEntity r, MediumEntity rel) {
        List<MbTrack> tracks = rel.getTracks().stream()
            .map(TrackEntity::toMbTrack)
            .collect(Collectors.toList());
        tracks.forEach(t -> {
            t.setArtistMbib(artistEntity.getMbid().toString());
            t.setAlbumId(r.getId());
            t.setAlbumMbid(r.getMbid().toString());
        });
        mbTrackRepository.save(tracks);
        System.out.println(String.format("Stored %s tracks for album : %s and artist : %s", tracks.size(), r.getName(), artistEntity.getName()));
        return tracks;
    }

}
