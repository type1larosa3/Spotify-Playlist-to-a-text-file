package com.spotifytotext;

import com.spotifytotext.SpotifyLogic;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class App 
{
    public static void main( String[] args )
    {
        JFrame jFrame = new JFrame();
        jFrame.setSize(1000, 500);
        jFrame.setVisible(true);
        String[] clientInfo = getClientInfo(jFrame);

        SpotifyLogic spotifyLogic = new SpotifyLogic(clientInfo[0], clientInfo[1]);

        spotifyLogic.setAuthorizationCode(getAuthorizationCode(spotifyLogic, jFrame));

        spotifyLogic.finishAuthorization();

        String playlistID = getPlaylistIdFromUser(jFrame);
        int songCount = Integer.parseInt(getPlaylistSongCount(jFrame));
        String[] playlist = spotifyLogic.getPlaylist(playlistID, songCount);


    }

    private static String[] getClientInfo(JFrame frame) {

        String clientID = JOptionPane.showInputDialog(frame, "Enter Client ID", null, JOptionPane.PLAIN_MESSAGE);
        String clientSecret = JOptionPane.showInputDialog(frame, "Enter Client Secret", null, JOptionPane.PLAIN_MESSAGE);

        return new String[] {clientID, clientSecret};
    }

    private static String getAuthorizationCode(SpotifyLogic slogic, JFrame frame) {
        String redirectUri = slogic.getRedirectUri();

        JOptionPane.showMessageDialog(frame, new JTextArea("Copy the following URL into your browser and hit accept: " + redirectUri), null, JOptionPane.PLAIN_MESSAGE);
        String code = JOptionPane.showInputDialog(frame, "Copy the code in the new URL that loads into the box below and hit enter.", null, JOptionPane.PLAIN_MESSAGE);

        return code;
    }

    private static String getPlaylistIdFromUser(JFrame frame) {
        return JOptionPane.showInputDialog(frame, "Enter the playlist Id of the playlist you want to convert to text.", null, JOptionPane.PLAIN_MESSAGE);
    }

    private static String getPlaylistSongCount(JFrame frame) {
        return JOptionPane.showInputDialog(frame, "Enter the song count of the playlist ID you gave in the previous window.", null, JOptionPane.PLAIN_MESSAGE);
    }
}
