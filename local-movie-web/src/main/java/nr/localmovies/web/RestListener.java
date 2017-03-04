package nr.localmovies.web;

import nr.localmovies.boundary.MovieInfoBoundary;
import nr.localmovies.movieinfoapi.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class RestListener {
    private final MovieInfoBoundary movieInfoBoundary;
    private final MultipartFileSender fileSender;
    private static final Logger logger = Logger.getLogger(RestListener.class.getName());

    @Autowired
    public RestListener(MovieInfoBoundary movieInfoControl, MultipartFileSender fileSender){
        this.movieInfoBoundary = movieInfoControl;
        this.fileSender = fileSender;
    }

    /**
     * @param directoryPath - Path to directory you wish to list
     * @return - List of files in specified directory
     */
    @RequestMapping(value = "/titlerequest", produces="application/json")
    public List<MovieInfo> titleRequest(@RequestParam(value = "path") String directoryPath,
            HttpServletRequest request, HttpServletResponse response) throws ExecutionException {

        logger.log(Level.INFO, "Received request for - " + directoryPath + " from " + request.getRemoteAddr());
        response.addHeader("Access-Control-Allow-Origin", "*");
        return movieInfoBoundary.loadMovieList(directoryPath);
    }

    /**
     * @param moviePath - Path to video file to stream
     * throws Exception
     */
    @RequestMapping("/video.mp4")
    public void streamVideo(HttpServletResponse response, HttpServletRequest request,
                            @RequestParam("path") String moviePath) throws IOException {

        if(moviePath.contains("LocalMedia")) {
            logger.info("Streaming - " + moviePath + " to " + request.getRemoteAddr());
            response.addHeader("Access-Control-Allow-Origin", "*");
            fileSender.serveResource(Paths.get(moviePath), request, response);
        }
    }

    /**
     * @param moviePath - Path to video file
     * @return - Poster image for specified video file
     * throws Exception
     */
    @RequestMapping("/poster")
    public byte[] servePoster(@RequestParam("path") String moviePath, HttpServletResponse response) throws ExecutionException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        String image = movieInfoBoundary.loadSingleMovie(moviePath).getImage();
        if(image == null)
            return null;

        return Base64.getDecoder().decode(image);
    }
}