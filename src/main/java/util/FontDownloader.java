package util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Utility to download Vietnamese-compatible fonts for PDF generation
 */
public class FontDownloader {
    
    private static final Logger logger = Logger.getLogger(FontDownloader.class.getName());
    
    // Font URLs (using Google Fonts API or other free font sources)
    private static final String[] FONT_URLS = {
        // Noto Sans Vietnamese - Google Fonts
        "https://fonts.gstatic.com/s/notosans/v36/o-0IIpQlx3QUlC5A4PNr5TRASf6M7Q.woff2",
        // Roboto Vietnamese - Google Fonts  
        "https://fonts.gstatic.com/s/roboto/v30/KFOmCnqEu92Fr1Mu4mxK.woff2"
    };
    
    private static final String[] FONT_NAMES = {
        "NotoSans-Regular.woff2",
        "Roboto-Regular.woff2"
    };
    
    /**
     * Download fonts to resources directory
     */
    public static void downloadFonts() {
        logger.info("Starting font download...");
        
        try {
            // Create fonts directory if it doesn't exist
            Path fontsDir = Paths.get("src/main/resources/fonts");
            if (!Files.exists(fontsDir)) {
                Files.createDirectories(fontsDir);
                logger.info("Created fonts directory: " + fontsDir);
            }
            
            // Download each font
            for (int i = 0; i < FONT_URLS.length; i++) {
                downloadFont(FONT_URLS[i], FONT_NAMES[i], fontsDir);
            }
            
            // Create a simple TTF font file for testing
            createTestFont(fontsDir);
            
            logger.info("Font download completed successfully");
            
        } catch (Exception e) {
            logger.severe("Error downloading fonts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Download a single font file
     */
    private static void downloadFont(String fontUrl, String fileName, Path fontsDir) {
        try {
            Path fontPath = fontsDir.resolve(fileName);
            
            // Skip if file already exists
            if (Files.exists(fontPath)) {
                logger.info("Font already exists: " + fileName);
                return;
            }
            
            logger.info("Downloading font: " + fileName);
            
            URL url = new URL(fontUrl);
            try (InputStream in = url.openStream();
                 OutputStream out = Files.newOutputStream(fontPath)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
            logger.info("Successfully downloaded: " + fileName + " (" + Files.size(fontPath) + " bytes)");
            
        } catch (Exception e) {
            logger.warning("Failed to download font " + fileName + ": " + e.getMessage());
        }
    }
    
    /**
     * Create a simple font info file for reference
     */
    private static void createTestFont(Path fontsDir) {
        try {
            Path infoPath = fontsDir.resolve("font-info.txt");
            
            String fontInfo = "Vietnamese Font Support Information\n" +
                            "=====================================\n\n" +
                            "This directory contains fonts that support Vietnamese characters.\n\n" +
                            "Available fonts:\n" +
                            "- NotoSans-Regular.woff2: Google Noto Sans (Vietnamese support)\n" +
                            "- Roboto-Regular.woff2: Google Roboto (Vietnamese support)\n\n" +
                            "Fallback fonts (system):\n" +
                            "- Arial (Windows: C:/Windows/Fonts/arial.ttf)\n" +
                            "- Times New Roman (Windows: C:/Windows/Fonts/times.ttf)\n" +
                            "- Calibri (Windows: C:/Windows/Fonts/calibri.ttf)\n" +
                            "- DejaVu Sans (Linux: /usr/share/fonts/truetype/dejavu/)\n\n" +
                            "Vietnamese characters supported:\n" +
                            "- Vowels with diacritics: à á ả ã ạ ă ằ ắ ẳ ẵ ặ â ầ ấ ẩ ẫ ậ\n" +
                            "- è é ẻ ẽ ẹ ê ề ế ể ễ ệ ì í ỉ ĩ ị ò ó ỏ õ ọ ô ồ ố ổ ỗ ộ\n" +
                            "- ơ ờ ớ ở ỡ ợ ù ú ủ ũ ụ ư ừ ứ ử ữ ự ỳ ý ỷ ỹ ỵ\n" +
                            "- Special consonant: đ Đ\n\n" +
                            "Generated on: " + java.time.LocalDateTime.now() + "\n";
            
            Files.write(infoPath, fontInfo.getBytes("UTF-8"));
            logger.info("Created font info file: " + infoPath);
            
        } catch (Exception e) {
            logger.warning("Failed to create font info file: " + e.getMessage());
        }
    }
    
    /**
     * Check if Vietnamese fonts are available
     */
    public static boolean checkVietnameseFonts() {
        logger.info("Checking Vietnamese font availability...");
        
        Path fontsDir = Paths.get("src/main/resources/fonts");
        boolean hasResourceFonts = false;
        boolean hasSystemFonts = false;
        
        // Check resource fonts
        if (Files.exists(fontsDir)) {
            try {
                long fontCount = Files.list(fontsDir)
                    .filter(path -> path.toString().toLowerCase().endsWith(".ttf") || 
                                   path.toString().toLowerCase().endsWith(".woff2"))
                    .count();
                hasResourceFonts = fontCount > 0;
                logger.info("Found " + fontCount + " font files in resources");
            } catch (Exception e) {
                logger.warning("Error checking resource fonts: " + e.getMessage());
            }
        }
        
        // Check system fonts
        String[] systemFontPaths = {
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/times.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
        };
        
        for (String fontPath : systemFontPaths) {
            if (Files.exists(Paths.get(fontPath))) {
                hasSystemFonts = true;
                logger.info("Found system font: " + fontPath);
                break;
            }
        }
        
        boolean result = hasResourceFonts || hasSystemFonts;
        logger.info("Vietnamese font check result: " + (result ? "AVAILABLE" : "NOT AVAILABLE"));
        
        return result;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        logger.info("Vietnamese Font Downloader");
        logger.info("==========================");
        
        // Check current font availability
        checkVietnameseFonts();
        
        // Download fonts if needed
        if (args.length > 0 && "download".equals(args[0])) {
            downloadFonts();
            
            // Check again after download
            checkVietnameseFonts();
        } else {
            logger.info("To download fonts, run: java util.FontDownloader download");
        }
    }
}
