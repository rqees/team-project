package data_access;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import use_case.load_api.LoadApiDataGateway;

public class ApiDataAccessObject implements LoadApiDataGateway {
    private static final String SEARCH_API =
            "https://ckan0.cf.opendata.inter.prod-toronto.ca/api/3/action/package_search?q=";

    private static final String PACKAGE_API =
            "https://ckan0.cf.opendata.inter.prod-toronto.ca/api/3/action/package_show?id=";

    @Override
    public String getCsv(String datasetName) {
        String rtrn;
        try {
            final String datasetId = searchDataset(datasetName);
            if (datasetId == null) {
                rtrn = "Dataset not found.";
            }
            else {
                final String csvUrl = findCsvResource(datasetId);
                if (csvUrl == null) {
                    rtrn = "Dataset found, but no CSV resource available.";
                }
                else {
                    rtrn = fetchUrl(csvUrl);
                }
            }
        }
        catch (IOException ex) {
            rtrn = "Error: " + ex.getMessage();
        }
        return rtrn;
    }

    // Search dataset using CKAN full-text search
    private static String searchDataset(String name) throws IOException {
        final String json = fetchUrl(SEARCH_API + name.replace(" ", "+"));

        final JSONObject root = new JSONObject(json);
        final JSONArray results = root.getJSONObject("result").getJSONArray("results");

        final String rtrn;
        if (results.isEmpty()) {
            rtrn = null;
        }
        else {
            rtrn = results.getJSONObject(0).getString("id");
        }
        return rtrn;
    }

    // Retrieve dataset info and find the first CSV resource
    private static String findCsvResource(String datasetId) throws IOException {
        final String json = fetchUrl(PACKAGE_API + datasetId);

        final JSONObject root = new JSONObject(json);
        final JSONArray resources = root.getJSONObject("result").getJSONArray("resources");

        String rtrn = null;
        for (int i = 0; i < resources.length(); i++) {
            final JSONObject res = resources.getJSONObject(i);
            final String format = res.optString("format").toLowerCase();

            if ("csv".equals(format)) {
                rtrn = res.getString("url");
                break;
            }
        }
        return rtrn;
    }

    // HTTP GET helper
    private static String fetchUrl(String urlString) throws IOException {
        final URL url = new URL(urlString);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        final StringBuilder sb = new StringBuilder();
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
