package com.spotifytotext;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URI;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

public class SpotifyLogic {

    private static SpotifyApi spotifyApi;

    private static AuthorizationCodeUriRequest authorizationCodeUriRequest;
    private static String authorizationCode;
    private static AuthorizationCodeCredentials authorizationCodeCredentials;

    //setup to get code
    public SpotifyLogic(String clientId, String clientSecret) {
        spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret)
        .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:8888/callback")).build();

        authorizationCodeUriRequest = spotifyApi.authorizationCodeUri().build();
    }

    //provides the redirect URI
    public String getRedirectUri() {
        final URI uri = authorizationCodeUriRequest.execute();

        return uri.toString();
    }

    public void setAuthorizationCode(String code) {
        authorizationCode = code;
        System.out.println(authorizationCode);
    }

    public void finishAuthorization() {
        final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authorizationCode).build();

        try {
            authorizationCodeCredentials = authorizationCodeRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Access Token" + authorizationCodeCredentials.getAccessToken());
        }
        catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //API only allows for grabbing 100 songs at a time. 
    //Kee's playlist is way longer than that so I'm going to have to find some kind of work around.
    //turns out you need to index it.  
    public String[] getPlaylist(String playlistID, int songCount) {
        String[] trackList = new String[songCount];
        int counter = 0;


        while(trackList[trackList.length - 1] == null) {
            try {
                final GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistID).offset(counter).limit(100).build();
                final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsItemsRequest.execute();

                PlaylistTrack[] serverList = playlistTrackPaging.getItems();

                for(int i = 0; i < serverList.length; i++) {
                    trackList[counter] = serverList[i].getTrack().getName();
                    System.out.println(serverList[i].getTrack().getName());
                    counter++;
                }

                System.out.println("Call with " + serverList.length + " songs.");

            }
            catch (IOException | ParseException | SpotifyWebApiException e) {
                System.out.println("Error" + e.getMessage());
                return null;
            }
        }

        return trackList;
    }

}
