package nr.localmovies.movieinfoapi;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieInfoRepository extends CrudRepository<MovieInfoEntity, String> {

}
