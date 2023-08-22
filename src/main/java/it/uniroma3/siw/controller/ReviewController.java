package it.uniroma3.siw.controller;


import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Player;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.PlayerRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReviewController {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewValidator reviewValidator;
    @Autowired
    private GlobalController globalController;

    @PostMapping("/user/uploadReview/{playerId}")
    public String newReview(Model model, @Valid @ModelAttribute("review") Review review, BindingResult bindingResult, @PathVariable("playerId") Long id) {
        this.reviewValidator.validate(review,bindingResult);
        Player player = this.playerRepository.findById(id).get();
        if(!bindingResult.hasErrors()){
            if(this.globalController.getUser() != null && !player.getReviews().contains(review)){
                review.setAuthor(this.globalController.getUser().getUsername());
                review.setReviewedPlayer(playerRepository.findById(id).orElse(null));
                this.reviewRepository.save(review);
                player.getReviews().add(review);

            }
        }
        this.playerRepository.save(player);

        return this.playerService.function(model, player, this.globalController.getUser());

    }

    @GetMapping("/admin/deleteReview/{playerId}/{reviewId}")
    public String removeReview(Model model, @PathVariable("playerId") Long playerId,@PathVariable("reviewId") Long reviewId){
        Player player = this.playerRepository.findById(playerId).get();
        Review review = this.reviewRepository.findById(reviewId).get();

        player.getReviews().remove(review);
        this.reviewRepository.delete(review);
        this.playerRepository.save(player);
        return this.playerService.function(model, player, this.globalController.getUser());
    }

    @GetMapping("/user/modifyingReview/{playerId}/{reviewId}")
    public String updateReview(Model model, @PathVariable("playerId") Long playerId,@PathVariable("reviewId") Long reviewId){
        Player player = this.playerRepository.findById(playerId).get();
        Review review = this.reviewRepository.findById(reviewId).get();

        model.addAttribute("review", review);
        model.addAttribute("player", player);
        model.addAttribute("username", this.globalController.getUser().getUsername());

        return "formUpdateReview.html";
    }

    @PostMapping("/user/updateReview/{playerId}/{reviewId}")
    public String updateReview(Model model, @Valid @ModelAttribute("review") Review updatedReview, BindingResult bindingResult,@PathVariable("playerId") Long playerId, @PathVariable("reviewId") Long reviewId) {
        this.reviewValidator.validate(updatedReview, bindingResult);

        if (!bindingResult.hasErrors()) {
            Review existingReview = this.reviewRepository.findById(reviewId).orElse(null);

            if (existingReview != null) {
                existingReview.setTitle(updatedReview.getTitle());
                existingReview.setText(updatedReview.getText());
                existingReview.setRating(updatedReview.getRating());

                this.reviewRepository.save(existingReview);

            }
        }

        Player player = this.playerRepository.findById(playerId).orElse(null);
        return this.playerService.function(model, player, this.globalController.getUser());
    }

}
