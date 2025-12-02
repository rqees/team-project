package data_access;

import org.json.JSONArray;
import org.json.JSONObject;
import use_case.load_api.LoadAPIDataGateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIDataAccessObject implements LoadAPIDataGateway {
    private static final String SEARCH_API =
            "https://ckan0.cf.opendata.inter.prod-toronto.ca/api/3/action/package_search?q=";

    private static final String PACKAGE_API =
            "https://ckan0.cf.opendata.inter.prod-toronto.ca/api/3/action/package_show?id=";

    @Override
    public String getCSV(String datasetName) {
        try {
            String datasetId = searchDataset(datasetName);
            if (datasetId == null) {
                return "Dataset not found.";
            }

            String csvUrl = findCsvResource(datasetId);
            if (csvUrl == null) {
                return "Dataset found, but no CSV resource available.";
            }

            return fetchUrl(csvUrl);

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Search dataset using CKAN full-text search
    private static String searchDataset(String name) throws IOException {
        String json = fetchUrl(SEARCH_API + name.replace(" ", "+"));

        JSONObject root = new JSONObject(json);
        JSONArray results = root.getJSONObject("result").getJSONArray("results");

        if (results.isEmpty()) return null;

        return results.getJSONObject(0).getString("id");
    }

    // Retrieve dataset info and find the first CSV resource
    private static String findCsvResource(String datasetId) throws IOException {
        String json = fetchUrl(PACKAGE_API + datasetId);

        JSONObject root = new JSONObject(json);
        JSONArray resources = root.getJSONObject("result").getJSONArray("resources");

        for (int i = 0; i < resources.length(); i++) {
            JSONObject res = resources.getJSONObject(i);
            String format = res.optString("format").toLowerCase();

            if (format.equals("csv")) {
                return res.getString("url");
            }
        }

        return null;
    }

    // HTTP GET helper
    private static String fetchUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
