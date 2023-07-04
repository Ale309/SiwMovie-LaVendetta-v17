package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.transaction.Transactional;

@Service
public class MovieService {

	@Autowired
	private MovieRepository movieRepository;
	@Autowired
	private ArtistRepository artistRepository;
	@Autowired
	private ImageRepository imageRepository;

	@Transactional
	public Movie createNewMovie(Movie movie, MultipartFile[] multipartFile) {
		try {
			Set<Image> immagini = new HashSet<>();
			for(MultipartFile file : multipartFile)
				immagini.add(imageRepository.save(new Image(file.getBytes())));
			movie.setImages(immagini);
        }
        catch (IOException e){}
		this.movieRepository.save(movie);
		return movie;
	}
	
    @Transactional
    public void updateMovie(Movie movie) {
        movieRepository.save(movie);
    }
    
    @Transactional
	public Movie findById(Long id) {
		return this.movieRepository.findById(id).orElse(null);
	}

	public Iterable<Movie> findAllMovie(){
		return this.movieRepository.findAll();
	}

	public Movie saveMovie(Movie movie) {
		return this.movieRepository.save(movie);
	}

	public Movie saveDirectorToMovie(Long movieId, Long artistId) {
		Movie res = null;
		Artist director = this.artistRepository.findById(artistId).orElse(null);
		Movie movie = this.findById(movieId);
		if(movie != null && director != null) {
			movie.setDirector(director);
			this.saveMovie(movie);
			res = movie;
		}
		return res;
	}

	public Movie saveActorToMovie(Long movieId, Long artistId) {
		Movie movie = this.movieRepository.findById(movieId).orElse(null);
		Artist actor = this.artistRepository.findById(artistId).orElse(null);
		if(movie != null && actor != null) {
			Set<Artist> actors = movie.getActors();
			actors.add(actor);
			this.movieRepository.save(movie);
		}
		return movie;
	}
	
	public Movie deleteActorFromMovie(Long movieId, Long artistId) {
		Movie movie = this.movieRepository.findById(movieId).orElse(null);
		Artist actor = this.artistRepository.findById(artistId).orElse(null);
		if(movie != null && actor != null) {
			Set<Artist> actors = movie.getActors();
			actors.remove(actor);
			this.movieRepository.save(movie);
		}
		return movie;
	}

	public List<Movie> findByYear(Integer year) {
		return this.movieRepository.findByYear(year);
	}
	

    public String function(Model model, Movie movie, String username){
        Set<Artist> movieCast = new HashSet<>();
        if(movie.getActors() != null)
            movieCast.addAll(movie.getActors());
        movieCast.add(movie.getDirector());
        movieCast.remove(null);
        model.addAttribute("movieCast", movieCast);
        model.addAttribute("movie", movie);
        model.addAttribute("director", movie.getDirector());
        if(username != null && this.alreadyReviewed(movie.getReviews(),username))
            model.addAttribute("hasNotAlredyCommented", false);
        else
            model.addAttribute("hasNotAlreadyCommented", true);
        model.addAttribute("review", new Review());
        model.addAttribute("reviews", movie.getReviews());
        model.addAttribute("hasReviews", !movie.getReviews().isEmpty());

        return "movie.html";
    }

    @Transactional
    public boolean alreadyReviewed(Set<Review> reviews,String username){
        if(reviews != null)
            for(Review rev : reviews)
                if(rev.getUsername().equals(username))
                    return true;
        return false;
    }


   public long count() {

        return movieRepository.count();
    }

}
