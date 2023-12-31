package sagan.site.blog.support;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import sagan.site.blog.Post;
import sagan.site.blog.PostCategory;
import sagan.site.blog.BlogService;
import sagan.site.blog.PostMovedException;
import sagan.site.support.DateFactory;
import sagan.site.support.nav.PageableFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sagan.site.support.nav.PaginationInfo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller that handles requests for Atom feeds of blog content. All requests return a
 * single {@link Page} of content, the size of which is defined by
 * {@link PageableFactory#forFeeds()}.
 *
 * @see AtomFeedView
 */
@Controller
class AtomFeedController {

    private final BlogService blogService;
    private final AtomFeedView atomFeedView;

    private DateFactory dateFactory;

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    private final String ALL_POSTS_CATEGORY = "All Posts";
    private final String BROADCASTS_CATEGORY = "Broadcasts";


    @Autowired
    public AtomFeedController(BlogService blogService, SiteUrl siteUrl, DateFactory dateFactory) {
        this.blogService = blogService;
        this.atomFeedView = new AtomFeedView(siteUrl, dateFactory);
    }

    @GetMapping("/blog.atom")
    public AtomFeedView listPublishedPosts(Model model, HttpServletResponse response) {
        Page<Post> page = blogService.getPublishedPosts(PageableFactory.forFeeds());
        prepareResponse(model, response, page, "", "");
        return atomFeedView;
    }

	@GetMapping("/blog/category/{category}.atom")
    public AtomFeedView listPublishedPostsForCategory(@PathVariable PostCategory category, Model model,
                                                      HttpServletResponse response) {
        Page<Post> page = blogService.getPublishedPosts(category, PageableFactory.forFeeds());
        prepareResponse(model, response, page, category.getDisplayName(), "/category/" + category.getUrlSlug());
        return atomFeedView;
    }

	@GetMapping("/blog/broadcasts.atom")
    public AtomFeedView listPublishedBroadcastPosts(Model model, HttpServletResponse response) {
        Page<Post> page = blogService.getPublishedBroadcastPosts(PageableFactory.forFeeds());
        prepareResponse(model, response, page, "Broadcasts", "/broadcasts");
        return atomFeedView;
    }

    private AtomFeedView prepareResponse(Model model, HttpServletResponse response, Page<Post> page,
                                 String category, String subPath) {
        response.setCharacterEncoding("utf-8");
        model.addAttribute("posts", page.getContent());
        model.addAttribute("feed-title", ("Spring " + category).trim());
        String blogPath = "/blog" + subPath;
        model.addAttribute("blog-path", blogPath);
        model.addAttribute("feed-path", blogPath + ".atom");
        return atomFeedView;
    }
}
