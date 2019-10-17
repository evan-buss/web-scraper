# web-scraper
Breadth first search web scraper written in Java using the JavaFX toolkit.

![demo](https://media.giphy.com/media/hSLIpqLXcMAEl5pgic/giphy.gif)

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
- Graph View
    - Get a deep understanding of the path the crawler took in a visual format
    - Click any node to see the URL and data scraped from it
    - Entertaining to watch

## GUI

### Settings View
![settings view](https://raw.githubusercontent.com/evan-buss/web-scraper-swing/master/screenshot/settings.png)

### HTML View
![html view](https://raw.githubusercontent.com/evan-buss/web-scraper-swing/master/screenshot/html.png)

### JSON Selectors View
![json view](https://raw.githubusercontent.com/evan-buss/web-scraper-swing/master/screenshot/json.png)

#### Syntax
- Special Selectors `[type]`
    - `title` - get the page's title
    - `url` - get the page`s url
- Data Selectors `[css selector]:[type]`
    - CSS Selectors
        - Use css syntax to select elements
        - `div.class_name > h1` selects an h1 with a parent div of class `class_name`
    - Types
        - `text` - get all text between the given element
            - `<p>Hello <e>World!</e></p>` -> `Hello World!`
        - `owntext` - get only the text between the given element
            - `<p>Hello <e>World!</e></p>` -> `Hello`
        - `href`

#### Structure
- The selector should be a valid JSON object. It must have a `data` and `links` tag.
- Each data element has a custom title to identify it
- The links tag is an array of selectors pointing to anchor tags
    - The crawler will use these links' `href` attribute to traverse the internet.
    - If you do not care what links you are selecting just use `a`
    
### Scraped Graph
![graph view](https://raw.githubusercontent.com/evan-buss/web-scraper-swing/master/screenshot/graph.png)

#### Usage

- Nodes are added to the graph in real time and in the order they are traversed
- Select a node to view the URL and data associated with it in the dropdown
- Nodes are colored according to depth. Nodes of the same color were found at the same depth 

## Libraries
- Gradle
- JSoup
- Guava
- Lombok
- Gson
- JavaFX 
- GraphStream
- sjl4j / logback

## Development
- The project uses the Gradle build system. Simply import the project into any IDE and run the "application -> run" task

## Usage
- Download a prebuilt binary to run on any platform with 0 dependencies
- `java -jar [jarfile].jar`

## Initial Swing GUI
![screenshot](https://raw.githubusercontent.com/evan-buss/web-scraper-swing/master/screenshot/main.png)


