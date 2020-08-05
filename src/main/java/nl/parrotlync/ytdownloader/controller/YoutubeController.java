package nl.parrotlync.ytdownloader.controller;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/download")
public class YoutubeController {
    private YoutubeDownloader downloader = new YoutubeDownloader();
    private File outputDir = new File("download");
    private String host = "https://drive.ipictserver.nl/yt/";

    @GetMapping
    public String download(@RequestParam(value="query") String query) throws YoutubeException, IOException {
        if (query != null && !query.isEmpty()) {
            String id = getVideoID(query);
            File check = new File("download/" + id + ".m4a");
            if (!check.exists()) {
                YoutubeVideo video = downloader.getVideo(id);
                File output = video.download(video.audioFormats().get(0), outputDir, id, true);
                return host + output.getName();
            } else {
                return host + id + ".m4a";
            }
        }
        return "Please specify a query!";
    }

    private String getVideoID(String url) {
        // https://www.youtube.com/watch?v=c2rgrqEFVKc
        // https://youtu.be/c2rgrqEFVKc
        String id = url.replace("https://", "");
        id = id.replace("http://", "");
        id = id.replace("www.", "");
        id = id.replace("youtu.be", "");
        id = id.replace("youtube.com", "");
        id = id.replace("/", "");
        id = id.replace("watch?v=", "");
        if (id.contains("&")) {
            id = id.split("&")[0];
        }
        return id;
    }
}
