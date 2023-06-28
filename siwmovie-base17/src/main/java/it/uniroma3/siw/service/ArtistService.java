package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import jakarta.transaction.Transactional;

@Service
public class ArtistService {
	
	@Autowired
	private ArtistRepository artistRepository;
	
	@Transactional
	public boolean createNewArtist(Artist artist) {
		boolean res = false;
		if(!this.artistRepository.existsByNameAndSurname(artist.getName(), artist.getSurname()))
			res = true;
			artistRepository.save(artist);
		return res;
	}
	
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

	public String findArtistById(Long id) {
		this.artistRepository.findById(id).get();
		return null;
	}
}
