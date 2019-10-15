# web-scraper
Breadth first search web scraper written in Java using the JavaFX toolkit.

## GUI
### Settings View
![settings view](https://raw.githubusercontent.com/evan-buss/web-scraper-swing/master/screenshot/settings.png)
### HTML View
![html view](https://raw.githubusercontent.com/evan-buss/web-scraper-swing/master/screenshot/html.png)
### JSON Selectors View
![json view](https://raw.githubusercontent.com/evan-buss/web-scraper-swing/master/screenshot/json.png)

## Features

- Breadth first search from a starting URL
- Customizable parsing settings
    - Number of parallel threads
    - Maximum link traversal depth
    - Crawler timeout (lifetime)
    - Delay between requests
    - Optional to clear parsing queue before finishing
        - This will take a long time
- Keep track of parsing status with simple statistics
    - Total crawling time
    - Number of unique pages saved
    - Number of pages visited / number of pages queued
- Output scraped data to a JSON file
- View base url's HTML code to determine selectors
- Selector view
    - Set the JSON output format by settings variable names and CSS selectors
    - Interactively test your selectors before starting the crawl

## Libraries

- JSoup
- Guava
- Lombok
- Gson
- JavaFX 

## Old Swing GUI
![screenshot](https://raw.githubusercontent.com/evan-buss/web-scraper-swing/master/screenshot/main.png)


