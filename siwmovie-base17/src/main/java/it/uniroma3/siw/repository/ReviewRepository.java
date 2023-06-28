package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Review;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review,Long> {

public boolean existsByUsernameAndTitleAndRatingAndDescription(String username,String title,Integer rating, String description);

    public Review findByUsername(String username);



}
