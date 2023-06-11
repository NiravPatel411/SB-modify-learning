package sagan.site.webapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sagan.site.blog.BlogService;
import sagan.site.blog.Post;
import sagan.site.blog.PostCategory;
import sagan.site.blog.support.PostView;
import sagan.site.support.DateFactory;
import sagan.site.support.nav.PageableFactory;
import sagan.site.support.nav.PaginationInfo;
import sagan.site.webapi.project.ProjectMetadataController;
import sagan.site.webapi.repository.RepositoryMetadataController;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * Lists all resources at the root of the Web API
 */
@Controller
class IndexController {

	private final String ALL_POSTS_CATEGORY = "All Posts";

	private final BlogService blogService;

	private final DateFactory dateFactory;

	IndexController(BlogService blogService, DateFactory dateFactory) {
		this.blogService = blogService;
		this.dateFactory = dateFactory;
	}

	@GetMapping(path = "/api", produces = MediaTypes.HAL_JSON_VALUE)
	public RepresentationModel index() {
		return RepresentationModel.of(null).add(
				linkTo(methodOn(ProjectMetadataController.class).listProjects()).withRel("projects"),
				linkTo(methodOn(RepositoryMetadataController.class).listRepositories()).withRel("repositories")
		);
	}

	@GetMapping(path = "")
	public String listPublishedPosts(Model model, @RequestParam(defaultValue = "1") int page) {
		System.out.println("************************************************");
		Pageable pageRequest = PageableFactory.forLists(page);
		Page<Post> result = blogService.getPublishedPosts(pageRequest);
		renderListOfPosts(result, model, ALL_POSTS_CATEGORY);
		System.out.println("result : "+result.get().toString());
		System.out.println("************************************************");
		return "pages/index";
	}

	private void renderListOfPosts(Page<Post> page, Model model, String activeCategory) {
		Page<PostView> postViewPage = PostView.pageOf(page, dateFactory);
		List<PostView> posts = postViewPage.getContent();
		if (page.isFirst() && ALL_POSTS_CATEGORY.equals(activeCategory)) {
			List<PostView> recentPosts = new ArrayList<>(posts);
			model.addAttribute("newestPost", recentPosts.remove(0));
			model.addAttribute("posts", recentPosts);
		}
		else {
			model.addAttribute("posts", posts);
		}

		model.addAttribute("activeCategory", activeCategory);
		model.addAttribute("categories", PostCategory.values());
		model.addAttribute("paginationInfo", new PaginationInfo(postViewPage));
		model.addAttribute("disqusShortname", blogService.getDisqusShortname());
	}
}
