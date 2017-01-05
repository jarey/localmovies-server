package nr.localmovies.omdbmovieinfoprovider;

import com.google.common.io.ByteStreams;

import nr.localmovies.movieinfoapi.MovieInfo;
import nr.localmovies.movieinfoapi.IMovieInfoProvider;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.logging.Logger;

public class OMDBIMovieInfoProvider implements IMovieInfoProvider {

    private static Logger logger = Logger.getLogger(OMDBIMovieInfoProvider.class.getName());

    @Override
    public MovieInfo getMovieInfo(String title, String currentPath){
        return getInfoFromOMDB(title, currentPath);
    }

    private MovieInfo getInfoFromOMDB(String title, String currentPath) {

        List<MovieInfo> movieDataList = new ArrayList<>();

        JSONObject jsonObject = getData(title, currentPath);
        MovieInfo.Builder movieInfoBuilder = MovieInfo.Builder.newInstace();
        movieInfoBuilder.setTitle(title);
        try {
            movieInfoBuilder.setImage(Base64.getEncoder().encodeToString(getImage(jsonObject)));
        } catch (Exception e) {
            movieInfoBuilder.setImage(null);
        }
        try {
            movieInfoBuilder.setIMDBRating(jsonObject.getString("imdbRating"));
        } catch (Exception e) {
            movieInfoBuilder.setIMDBRating("N/A");
        }
        try {
            movieInfoBuilder.setMetaRating(jsonObject.getString("Metascore"));
        } catch (Exception e) {
            movieInfoBuilder.setMetaRating("N/A");
        }
        try {
            movieInfoBuilder.setReleaseYear(jsonObject.getString("Year"));
        } catch (Exception e) {
            movieInfoBuilder.setReleaseYear("N/A");
        }

        return movieInfoBuilder.build();
    }

    private JSONObject getData(String title, String currentPath) {
        String uri = "http://www.omdbapi.com/?t=";
        String currentPathLowerCase = currentPath.toLowerCase();

        if(currentPathLowerCase.contains("season") || currentPathLowerCase.contains("movies")) {
            title = title.substring(0, title.length() - 4);
        }
        try {
            URL url = new URL(uri + title.replace(" ", "%20"));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String end = "";
            String string = br.readLine();
            while (!(string == null)) {
                end = end + string;
                string = br.readLine();
            }
            br.close();
            urlConnection.disconnect();

            return new JSONObject(end);
        } catch (Exception e) {
            logger.severe(e.toString());
        }
        return null;
    }

    private byte[] getImage(JSONObject jsonObject) {
        try {
            URL imageURL = new URL(jsonObject.get("Poster").toString());
            InputStream is = imageURL.openConnection().getInputStream();
            return ByteStreams.toByteArray(is);
        } catch (Exception e) {
            logger.severe(e.toString());
        }
        return null;
    }
}
