package com.liberty.service;

import com.liberty.common.PleerLinkFetcher;
import com.liberty.model.GenericTrack;
import com.liberty.model.MbArtist;
import com.liberty.model.MbTrack;
import com.liberty.model.PleerTrack;
import com.liberty.model.StreamPlatform;
import com.liberty.model.StreamTrack;
import com.liberty.model.ZaycevTrack;
import com.liberty.repository.GenericTrackRepository;
import com.liberty.repository.MbArtistRepository;
import com.liberty.repository.MbTrackRepository;
import com.liberty.repository.PleerArtistRepository;
import com.liberty.repository.PleerTrackRepository;
import com.liberty.repository.ZaycevTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public void fetchPleerStreams() {
        PleerLinkFetcher fetcher = new PleerLinkFetcher();
        int page = 0;
        int size = 1000;
        boolean completed = false;
        AtomicInteger counter = new AtomicInteger(page * size);
        long total = genericTrackRepository.count();
        while (!completed) {
            List<GenericTrack> all = genericTrackRepository.findAll(new PageRequest(page, size)).getContent();
            all.forEach(t -> {
                if (t.getStreams() != null) {
                    t.getStreams().stream().filter(s -> s.getPlatform() == StreamPlatform.PLEER && StringUtils.isEmpty(s.getStreamLink()))
                        .forEach(s -> {
                            try {
                                Optional<String> link = fetcher.fetchLink(s.getPlatformTrackId());
                                link.ifPresent(l -> {
                                    s.setStreamLink(l);
                                    System.out.println("Fetched stream for : " + s.getTrackName());
                                });
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                        });
                    genericTrackRepository.save(t);
                }
                System.out.println(String.format("Processed %s / %s tracks", counter.incrementAndGet(), total));
            });
            page++;
            if (all.size() < size) {
                completed = true;
            }
        }
    }

    public void linkTracksNew() {
        Map<String, String> mbArtists = loadMbArtists();
        Map<String, List<StreamTrack>> zaycevArtists = loadZaycevArtists();
        Map<String, List<StreamTrack>> pleerArtists = loadPleerArtists();
        int page = 0;
        int size = 10000;
        boolean completed = false;
        AtomicInteger counter = new AtomicInteger(page * size);
        long total = mbTrackRepository.count();
        while (!completed) {
            List<MbTrack> all = mbTrackRepository.findAll(new PageRequest(page, size)).getContent();
            all.parallelStream().forEach(t -> {
                String artistName = mbArtists.get(t.getArtistMbib());
                List<StreamTrack> pleerStreams = null;
                List<StreamTrack> zaycevStreams = null;
                if (!StringUtils.isEmpty(artistName)) {
                    pleerStreams = pleerArtists.get(artistName);
                    zaycevStreams = zaycevArtists.get(artistName);
                }
                linkTracks(t, artistName, pleerStreams, zaycevStreams);
                System.out.println(String.format("Processed %s / %s tracks", counter.incrementAndGet(), total));
            });
            page++;
            if (all.size() < size) {
                completed = true;
            }
        }
    }

    private void linkTracks(MbTrack mbTrack, String artistName, List<StreamTrack> pleerStreams, List<StreamTrack> zaycevStreams) {
        GenericTrack genericTrack = new GenericTrack(mbTrack);
        genericTrack.addStreams(pleerStreams);
        genericTrack.addStreams(zaycevStreams);

        System.out.println(String.format("Found %s streams for track: %s and artist: %s", genericTrack.getStreams().size(), mbTrack.getName(), artistName));
        genericTrackRepository.save(genericTrack);
    }

    public Map<String, String> loadMbArtists() {
        Map<String, String> mbArtist = new HashMap<>();

        int page = 0;
        int size = 50000;
        boolean completed = false;
        AtomicInteger counter = new AtomicInteger(page * size);
        long total = mbArtistRepository.count();
        while (!completed) {
            List<MbArtist> all = mbArtistRepository.findAll(new PageRequest(page, size)).getContent();
            all.forEach(a -> {
                String name = cleanString(a.getName());
                mbArtist.put(a.getMbid(), name);
            });
            System.out.println(String.format("Loaded %s / %s musicbrainz artists", counter.addAndGet(all.size()), total));
            page++;
            if (all.size() < size) {
                completed = true;
            }
        }
        return mbArtist;
    }

    public Map<String, List<StreamTrack>> loadZaycevArtists() {
        Map<String, List<StreamTrack>> zaycevTrackMap = new HashMap<>();

        int page = 0;
        int size = 20000;
        boolean completed = false;
        AtomicInteger counter = new AtomicInteger(page * size);
        long total = zaycevTrackRepository.count();
        while (!completed) {
            List<ZaycevTrack> all = zaycevTrackRepository.findAll(new PageRequest(page, size));
            all.forEach(track -> {
                String artist = cleanString(track.getZaycevArtistName());
                List<StreamTrack> streamTracks = zaycevTrackMap.get(artist);
                if (streamTracks == null) {
                    ArrayList<StreamTrack> streams = new ArrayList<>();
                    streams.add(track.toStreamTrack());
                    zaycevTrackMap.put(artist, streams);
                } else {
                    streamTracks.add(track.toStreamTrack());
                    zaycevTrackMap.put(artist, streamTracks);
                }
            });
            System.out.println(String.format("Loaded %s / %s tracks", counter.addAndGet(all.size()), total));
            System.out.println(zaycevTrackMap.size() + " different artist for zaycev database");
            page++;
            if (all.size() < size) {
                completed = true;
            }
        }
        return zaycevTrackMap;
    }

    public Map<String, List<StreamTrack>> loadPleerArtists() {
        Map<String, List<StreamTrack>> pleerTrackMap = new HashMap<>();

        int page = 0;
        int size = 20000;
        boolean completed = false;
        AtomicInteger counter = new AtomicInteger(page * size);
        long total = pleerTrackRepository.count();
        while (!completed) {
            List<PleerTrack> all = pleerTrackRepository.findAll(new PageRequest(page, size));
            all.forEach(track -> {
                String singer = cleanString(track.getSinger());
                List<StreamTrack> streamTracks = pleerTrackMap.get(singer);
                if (streamTracks == null) {
                    ArrayList<StreamTrack> streams = new ArrayList<>();
                    streams.add(track.toStreamTrack());
                    pleerTrackMap.put(singer, streams);
                } else {
                    streamTracks.add(track.toStreamTrack());
                    pleerTrackMap.put(singer, streamTracks);
                }
            });
            System.out.println(String.format("Loaded %s / %s tracks", counter.addAndGet(all.size()), total));
            System.out.println(pleerTrackMap.size() + " different artists in pleer database");
            page++;
            if (all.size() < size) {
                completed = true;
            }
        }
        return pleerTrackMap;
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
