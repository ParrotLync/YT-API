package nl.parrotlync.ytdownloader.controller;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/download")
public class YoutubeController {
    private final YoutubeDownloader downloader = new YoutubeDownloader();
    private final File outputDir = new File("download");
    private String host = "https://drive.ipictserver.nl/yt/";

    @GetMapping
    public HashMap<String, String> download(@RequestParam(value="query", required=false) String query) throws YoutubeException, IOException {
        HashMap<String, String> response = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String id = getVideoID(query);
            File check = new File("download/" + id + ".m4a");
            if (!check.exists()) {
                YoutubeVideo video = downloader.getVideo(id);
                if (video.details().lengthSeconds() < 1801) {
                    File output = video.download(video.audioFormats().get(0), outputDir, id, true);
                    response.put("status", "OK");
                    response.put("msg", "Download successful!");
                    response.put("url", host + output.getName());
                } else {
                    response.put("status", "ERROR");
                    response.put("msg", "Video is too long! Maximum length is 30 minutes.");
                }
            } else {
                response.put("status", "OK");
                response.put("msg", "File already exists!");
                response.put("url", host + id + ".m4a");
            }
            return response;
        }
        response.put("status", "ERROR");
        response.put("msg", "Please specify a valid query!");
        return response;
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
