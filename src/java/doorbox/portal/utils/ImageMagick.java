/*
 * Copyright © Torin Walker 2008, 2009. All Rights Reserved.
 * No part of this document may be reproduced without
 * written consent from the author.
 */

package doorbox.portal.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author torinw
 */
public class ImageMagick {
    // This static version combines all the old separate commands into one magick binary with the first argument being
    // the original binary name less the hypen.
    // e.g.
    //   Old: "convert.exe -option arg -option arg ..."
    //   New: "magick.exe convert -option arg -option arg ..."
    public static final String MAGICK_BIN = "/www/bin/ImageMagick-7.0.2-Q8/magick.exe";

    // 10 seconds is the longest we'll wait to convert an image
    public static final long TIMEOUT = 30000;

    public static int normalizeImage(File in, File out, String comment) throws IOException {
        List<String> parameters = new ArrayList();
        
        parameters.add("-resize");
        parameters.add("240x320");
        parameters.add("-quality");
        parameters.add("85");
        parameters.add("-comment");
        parameters.add(comment);
        
        return convert(in, out, parameters);
    }

    public static int createThumbnail(File in, File out, String comment) throws IOException {
        List<String> parameters = new ArrayList();
        
        parameters.add("-thumbnail");
        parameters.add("75x100");
        parameters.add("-quality");
        parameters.add("85");
        parameters.add("-comment");
        parameters.add(comment);
        
        return convert(in, out, parameters);
    }

    private static int convert(File in, File out, List<String> parameters) throws IOException {
        List<String> command = new ArrayList();
        command.add(MAGICK_BIN);
        command.add("convert");
        command.add(in.getAbsolutePath());
        command.addAll(parameters);
        command.add(out.getAbsolutePath());
        
        ProcessBuilder pb = new ProcessBuilder(command);
        System.out.println(pb.command().toString());
        pb.redirectErrorStream(true);
        try {
            final Process p = pb.start();
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized(p) {
                        p.destroy();
                    }
                }
            }, TIMEOUT);
            p.waitFor();
            synchronized (p) {
                t.cancel();
            }
            return p.exitValue();
        } catch (InterruptedException e) {
            throw new IOException("convert exceeded " + TIMEOUT/1000 + " second timeout for command " + pb.command().toString());
        } catch (Exception e) {
            throw new IOException("Conversion failed", e);
        }
    }
}
