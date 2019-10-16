package com.evanbuss.webscraper.utils;

import com.evanbuss.webscraper.models.QueryModel;
import com.evanbuss.webscraper.models.ResultModel;
import com.evanbuss.webscraper.models.ResultModelAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class ParseUtils {

    private static final Gson gsonPretty =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(ResultModel.class, new ResultModelAdapter())
                    .create();
    private static final Gson gson = new Gson();

    private static final Logger logger = LoggerFactory.getLogger(ParseUtils.class);

    /**
     * Retrieve the HTML from the website located at URL
     *
     * @param url - website to load HTML from
     * @return - HTML string
     * @throws IOException - throws exception if the response code is not 200 or there are problems
     *                     parsing
     */
    public static String[] urlToHTML(String url) throws IOException {
        Connection.Response response =
                Jsoup.connect(url)
                        .userAgent(
                                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0")
                        .execute();

        //  TODO: Logging here
        if (response.statusCode() != 200) {
            logger.warn("Received a {} response when requesting {}", response.statusCode(), response.url());
        }

        Document document = response.parse();
        document.getElementsByTag("script").remove();
        return new String[]{document.toString(), document.baseUri()};
    }

    /**
     * Convert a page query to a page result using the given HTML string
     *
     * @param queryModel - query to run on each HTML page
     * @param html       - string of HTML
     * @return ResultModel containing the parsed data from the HTML
     */
    public static ResultModel queryToResult(QueryModel queryModel, String html, String baseURL) {
        ResultModel resultModel = new ResultModel();
        Document document = Jsoup.parse(html, baseURL);

        // Search for each query
        for (Map.Entry<String, String> queryPair : queryModel.data.entrySet()) {

            String[] query = queryPair.getValue().split(":");
            Elements elements = document.select(query[0]);

            for (Element elt : elements) {
                switch (query[1].toUpperCase()) {
                    case "HREF":
                        String href = elt.attr("href");
                        if (!href.equals("")) {
                            resultModel.data.put(queryPair.getKey(), href);
                        }
                        break;
                    case "TEXT":
                        resultModel.data.put(queryPair.getKey(), elt.text());
                        break;
                    default:
                        System.out.println("Invalid Query Type");
                }
            }
        }

        // Search for each link
        for (String linkSelector : queryModel.links) {
            Elements elements = document.select(linkSelector);
            for (Element elt : elements) {
                resultModel.links.add(elt.attr("abs:href"));
            }
        }

        return resultModel;
    }

    /**
     * Convert json string to QueryModel object
     *
     * @param json - json string
     * @return QueryModel representation of the string
     * @throws Exception - Invalid json string supplied causes exception
     */
    public static QueryModel jsonToQuery(String json) throws Exception {
        try {
            QueryModel queryModel = gson.fromJson(json, QueryModel.class);
            System.out.println(queryModel);
            return queryModel;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    /**
     * Convert a ResultModel to a json string
     *
     * @param resultModel - model to convert to JSON
     * @return json representation of the model
     */
    public static String resultToJSON(ResultModel resultModel) {
        return gsonPretty.toJson(resultModel);
    }
}
