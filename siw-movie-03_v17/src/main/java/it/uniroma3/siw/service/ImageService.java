package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ImageRepository;

@Service
public class ImageService {
	
	@Autowired
	private ImageRepository imageRepository;

	public Image findById(Long id) {
		return this.imageRepository.findById(id).get();
	}
	
	public Image saveImage(Image image) {
		return this.imageRepository.save(image);
	}
	

	public void deleteImage(Image image) {
		this.imageRepository.delete(image);
		
	}

}
