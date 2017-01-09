function weight = weightFeature(user,Movie)
a = user(1,:) ~= 0;
sum_movie_watch = sum(a');
features = a*Movie;
weight = features./sum_movie_watch;
end