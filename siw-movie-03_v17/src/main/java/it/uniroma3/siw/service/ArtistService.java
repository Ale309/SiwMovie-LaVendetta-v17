package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import jakarta.transaction.Transactional;

@Service
public class ArtistService {
	
	@Autowired
	private ArtistRepository artistRepository;
	@Autowired
	private ImageRepository imageRepository;
	
	@Transactional
	public Artist createNewArtist(Artist artist, MultipartFile multipartFile) {
		try {

            artist.setPicture(imageRepository.save(new Image(multipartFile.getBytes())));

        }
        catch (IOException e){}
        this.artistRepository.save(artist);
        return artist;
	}
	
	@Transactional
    public Artist saveArtist(Artist artist) {
        return this.artistRepository.save(artist);
    }
	
	public Iterable<Artist> findAllArtist(){
		return this.artistRepository.findAll();
	}
	

	@Transactional
	public List<Artist> findActorsNotInMovie(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : artistRepository.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}

	public Artist findArtistById(Long id) {
		return this.artistRepository.findById(id).get();
	}
}
