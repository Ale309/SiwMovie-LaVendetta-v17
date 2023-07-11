package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.MovieService;
import jakarta.validation.Valid;

@Controller
public class MovieController {

	@Autowired
	private MovieService movieService;

	@Autowired
	private ArtistService artistService;
	
	@Autowired 
	private MovieValidator movieValidator;
	
	@Autowired
	private GlobalController globalController;

	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = movieService.findById(id);
		if(movie != null) {
			model.addAttribute("movie", movieService.findById(id));
			
		}else {
			return "movieError.html";
		}
		return "admin/formUpdateMovie.html";

	}

	@GetMapping(value="/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}

	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieService.findAllMovie());
		return "admin/manageMovies.html";
	}

	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.saveDirectorToMovie(movieId, directorId);
		if(movie != null) {
			model.addAttribute("movie", movie);
			return "admin/formUpdateMovie.html";
		}
		else {
			return "movieError.html";
		}
	}


	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artists", artistService.findAllArtist());
		Movie movie = movieService.findById(id);
		if(movie != null) {
			model.addAttribute("movie", movie);
			return "admin/directorsToAdd.html";
		}else{
			return "movieError.html";
		}
	}

	@PostMapping("/admin/movie")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult,@RequestParam("movieImage") MultipartFile[] multipartFile, Model model) {

		this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {
			model.addAttribute("movie", this.movieService.createNewMovie(movie, multipartFile));
			return "movie.html";
		} else {
			return "movieError.html";
		}
	}

	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.findById(id);
		if(movie != null) {
			model.addAttribute("movie", movie);
			model.addAttribute("review",new Review());
			model.addAttribute("reviews",movie.getReviews());
			model.addAttribute("hasReviews",!movie.getReviews().isEmpty());

			if(this.globalController.getUser() != null && this.globalController.getUser().getUsername() != null && this.movieService.alreadyReviewed(movie.getReviews(),this.globalController.getUser().getUsername()))
	            model.addAttribute("hasNotAlreadyCommented", false);
	        else
	            model.addAttribute("hasNotAlreadyCommented", true);
			return "movie.html";

		}else {
			return "movieError.html";
		}
	}

	@GetMapping("/movie")
	public String getMovies(Model model) {
		model.addAttribute("nummovies",movieService.count());
		model.addAttribute("movies", this.movieService.findAllMovie());
		return "movies.html";
	}

	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam int year) {
		model.addAttribute("movies", this.movieService.findByYear(year));
		return "foundMovies.html";
	}

	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {

		List<Artist> actorsToAdd = this.artistService.findActorsNotInMovie(id);
		model.addAttribute("actorsToAdd", actorsToAdd);
		Movie movie = this.movieService.findById(id);
		if(movie != null) {
			model.addAttribute("movie", movie);
			return "admin/actorsToAdd.html";
		}else {
			return "movieError.html";
		}

	}

	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.saveActorToMovie(movieId, actorId);
		if(movie != null) {
			List<Artist> actorsToAdd = this.artistService.findActorsNotInMovie(movieId);
			model.addAttribute("movie", movie);
			model.addAttribute("actorsToAdd", actorsToAdd);
			return "admin/actorsToAdd.html";
		}else{
			return "movieError.html";
		}
	}

	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.deleteActorFromMovie(movieId, actorId);
		if(movie != null) {
			List<Artist> actorsToAdd = artistService.findActorsNotInMovie(movieId);
			model.addAttribute("movie", movie);
			model.addAttribute("actorsToAdd", actorsToAdd);
			return "admin/actorsToAdd.html";
		}else{
			return "movieError.html";
		}
	}
	


}
