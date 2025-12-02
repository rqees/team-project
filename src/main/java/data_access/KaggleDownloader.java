package data_access;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

/**
 * Utility class that talks directly to the Kaggle HTTP API to download CSV
 * files and returns a {@link File} pointing to the downloaded CSV.
 *
 * <p>Authentication is taken from environment variables so that end users
 * never have to paste their API key into the GUI:</p>
 *
 * <ul>
 *   <li>{@code KAGGLE_USERNAME} – your Kaggle username</li>
 *   <li>{@code KAGGLE_KEY} – your Kaggle API key</li>
 * </ul>
 *
 * <p>The existing CSV load use case is responsible for turning the downloaded
 * file into a DataSet.</p>
 */
public class KaggleDownloader {

    private static final String ENV_USERNAME = "KAGGLE_USERNAME";
    private static final String ENV_API_KEY = "KAGGLE_KEY";

    /**
     * Downloads a single CSV file from a Kaggle <strong>dataset</strong> using the
     * Kaggle HTTP API and returns the local {@link File}. The file is downloaded
     * into a temporary directory.
     *
     * <p>This method only supports datasets (not competitions).</p>
     *
     * @param kaggleDatasetId dataset id in the form {@code owner-slug/dataset-slug},
     *                        for example {@code "zynicide/wine-reviews"}.
     * @param fileName        the exact CSV file name inside the Kaggle dataset,
     *                        for example {@code "winemag-data_first150k.csv"}.
     * @return a {@link File} pointing to the downloaded CSV.
     * @throws IOException if the environment is not configured correctly or the
     *                     download fails for any reason.
     */
    public static File downloadCsv(String kaggleDatasetId,
                                   String fileName) throws IOException {

        String username = System.getenv(ENV_USERNAME);
        String apiKey = System.getenv(ENV_API_KEY);

        if (username == null || username.isBlank()) {
            throw new IOException("Environment variable " + ENV_USERNAME + " is not set.");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IOException("Environment variable " + ENV_API_KEY + " is not set.");
        }
        if (kaggleDatasetId == null || kaggleDatasetId.isBlank()) {
            throw new IOException("Kaggle dataset id must not be empty.");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IOException("File name must not be empty.");
        }

        String[] parts = kaggleDatasetId.split("/");
        if (parts.length != 2) {
            throw new IOException("Dataset id must be in the form 'owner-slug/dataset-slug'.");
        }
        String ownerSlug = parts[0];
        String datasetSlug = parts[1];

        // Example:
        // https://www.kaggle.com/api/v1/datasets/download/zynicide/wine-reviews/winemag-data_first150k.csv
        String apiUrl = "https://www.kaggle.com/api/v1/datasets/download/"
                + encodePathSegment(ownerSlug) + "/"
                + encodePathSegment(datasetSlug) + "/"
                + encodePathSegment(fileName);

        // Create an isolated temporary directory for this download.
        Path tempDir = Files.createTempDirectory("kaggle-download-");

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String credentials = username + ":" + apiKey;
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", basicAuth);

        int statusCode = connection.getResponseCode();
        if (statusCode != HttpURLConnection.HTTP_OK) {
            String errorMessage = "Failed to download from Kaggle (HTTP " + statusCode + ").";
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                byte[] buffer = new byte[4096];
                StringBuilder builder = new StringBuilder();
                int read;
                while ((read = errorStream.read(buffer)) != -1) {
                    builder.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
                }
                if (builder.length() > 0) {
                    errorMessage += " " + builder.toString().trim();
                }
            }
            connection.disconnect();
            throw new IOException(errorMessage);
        }

        File csvFile = tempDir.resolve(fileName).toFile();
        try (InputStream in = connection.getInputStream();
             OutputStream out = Files.newOutputStream(csvFile.toPath(),
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } finally {
            connection.disconnect();
        }

        if (!csvFile.exists() || !csvFile.isFile()) {
            throw new IOException("Download succeeded but file not found: " + csvFile.getAbsolutePath());
        }

        return csvFile;
    }

    /**
     * Very small helper to percent-encode path segments that may contain
     * characters outside the safe set.
     */
    private static String encodePathSegment(String segment) {
        StringBuilder sb = new StringBuilder();
        for (char c : segment.toCharArray()) {
            if (isUnreserved(c)) {
                sb.append(c);
            } else {
                sb.append(String.format("%%%02X", (int) c));
            }
        }
        return sb.toString();
    }

    private static boolean isUnreserved(char c) {
        return (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9')
                || c == '-' || c == '.' || c == '_' || c == '~';
    }
}


