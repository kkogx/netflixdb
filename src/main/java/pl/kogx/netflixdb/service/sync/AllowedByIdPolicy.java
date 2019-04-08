package pl.kogx.netflixdb.service.sync;

import java.util.function.Predicate;

public interface AllowedByIdPolicy extends Predicate<Long> {

    AllowedByIdPolicy ALL_ALLOWED = p -> true;
}
