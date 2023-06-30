package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	@Autowired
	private MovieService movieService;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private ReviewValidator reviewValidator;
	@Autowired
	private GlobalController globalController;


	@PostMapping("/user/uploadReview/{movieId}")
	public String newReview(Model model, @Valid @ModelAttribute("review") Review review, BindingResult bindingResult, @PathVariable("movieId") Long id) {
		this.reviewValidator.validate(review,bindingResult);
		if(!bindingResult.hasErrors()) {
			Movie movie = this.movieService.findById(id);
			if(this.globalController.getUser() != null && !movie.getReviews().contains(review)){
				review.setUsername(this.globalController.getUser().getUsername());
				this.reviewService.saveReview(review);
				movie.getReviews().add(review);
			}
			this.movieService.saveMovie(movie);
			return this.movieService.function(model, movie, this.globalController.getUser().getUsername());
		}else
			return "movieError.html";
	}
	
//	@PostMapping("/user/uploadReview/{movieId}")
//	public String newReview(Model model, @Valid @ModelAttribute("review") Review review, BindingResult bindingResult, @PathVariable("movieId") Long id) {
//		this.reviewValidator.validate(review,bindingResult);
//			Movie movie = this.movieService.findById(id);
//			if(this.globalController.getUser() != null && !movie.getReviews().contains(review)){
//				review.setUsername(this.globalController.getUser().getUsername());
//				this.reviewService.saveReview(review);
//				movie.getReviews().add(review);
//			}
//			this.movieService.saveMovie(movie);
//			return this.movieService.function(model, movie, this.globalController.getUser().getUsername());
//	}

	@GetMapping("/admin/deleteReview/{movieId}/{reviewId}")
	public String removeReview(Model model, @PathVariable("movieId") Long movieId,@PathVariable("reviewId") Long reviewId){
		Movie movie = this.movieService.findById(movieId);
		Review review = this.reviewService.findById(reviewId);

		movie.getReviews().remove(review);
		this.reviewService.deleteReview(review);
		this.movieService.saveMovie(movie);
		return this.movieService.function(model, movie, this.globalController.getUser().getUsername());
	}
}
