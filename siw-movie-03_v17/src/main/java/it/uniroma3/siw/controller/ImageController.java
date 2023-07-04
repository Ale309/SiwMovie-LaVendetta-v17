package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.MovieService;

@Controller
public class ImageController {
	
	@Autowired
    private ImageService imageService;
	
	@Autowired
	private MovieService movieService;
	

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> displayItemImage(@PathVariable("id") Long id) {
        Image img = this.imageService.findById(id);
        byte[] image = img.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }	

	@PostMapping("/admin/updateMovieImages/{movieId}")
	public String addImageToMovie(@PathVariable("movieId") Long movieId, @RequestParam("movieImage") MultipartFile[] multipartFile, Model model) {
		Movie movie = this.movieService.findById(movieId);
		Set<Image> immagini = new HashSet<>();
		try {
			
			for(MultipartFile file : multipartFile)
				immagini.add(imageService.saveImage(new Image(file.getBytes())));
			
        }
        catch (IOException e){}
		movie.addImages(immagini);
		this.movieService.saveMovie(movie);
		model.addAttribute("movie", movie);
		return "/admin/formUpdateMovie.html";

	}

	@GetMapping("/admin/deleteImage/{movieId}/{imageId}")
	public String removeImage(@PathVariable("movieId") Long movieId,@PathVariable("imageId") Long imageId, Model model){
		Movie movie = this.movieService.findById(movieId);
		Image image = this.imageService.findById(imageId);

		movie.getImages().remove(image);
		this.imageService.deleteImage(image);
		this.movieService.saveMovie(movie);
		model.addAttribute("movie", movie);
		return "/admin/formUpdateMovie.html";
	}

}
