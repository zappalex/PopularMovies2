# PopularMovies2
Udacity NanoDegree Project 2

This is project 2 for Udacity's Android NanoDegree. This project does the following: 
- Pulls a list of movies from TMDb ( The Move Database ) 
- Populates a grid view of movie posters 
- Allows users to pull most popular movies, top rated movies, or favorite movies
- Allows user to click on movie poster and be taken to a movie detail page, with info on the movie
- Pulls a list of movie clips for the selected movie, and displays all clip links in a list view on the detail page
- Pulls a list of movie reviews for the selected movie, and displays all reviews in a list view on the detail page
- Allows user to add or delete movie from favorites list
- Uses a content provider to manage user's favorite movies

This project does not employ any libraries other than Picasso for image retrieval.  Therefore, all 
other functionality relies on native android methods.  

Note:  The user will need to plug in an API key from https://www.themoviedb.org in order to use this project. 
Once the user obtains a key, they can simply add it to the NetworkUtils class. 
