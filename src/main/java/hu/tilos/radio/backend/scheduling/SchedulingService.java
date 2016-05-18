package hu.tilos.radio.backend.scheduling;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import hu.tilos.radio.backend.show.ShowType;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.encoding.EncodingManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SchedulingService {


    @Inject
    private DB db;

    @Inject
    private DozerBeanMapper mapper;


    public List<SchedulingWithShow> listSchedulings(Date now) {
        BasicDBObject d = new BasicDBObject();
        BasicDBList and = new BasicDBList();
        and.add(new BasicDBObject("schedulings.validTo", new BasicDBObject("$gt", now)));
        and.add(new BasicDBObject("schedulings.validFrom", new BasicDBObject("$lt", now)));
        and.add(new BasicDBObject("status", 1));
        d.append("$and", and);
        DBCursor result = db.getCollection("show").find(d);
        List<SchedulingWithShow> schedulings = StreamSupport.stream(result.spliterator(), false).flatMap(mongoObject -> {
            BasicDBList list = (BasicDBList) mongoObject.get("schedulings");
            return StreamSupport.stream(list.spliterator(), false).map(mongoScheduling -> {
                SchedulingWithShow sws = mapper.map(mongoScheduling, SchedulingWithShow.class);
                sws.setShowName(mongoObject.get("name").toString());
                sws.setShowType(ShowType.values()[(int) mongoObject.get("type")]);
                return sws;
            }).filter(schedulingWithShow -> schedulingWithShow.getValidFrom().before(now) && schedulingWithShow.getValidTo().after(now));
        }).collect(Collectors.toList());

        Collections.sort(schedulings, new Comparator<SchedulingWithShow>() {
            @Override
            public int compare(SchedulingWithShow o1, SchedulingWithShow o2) {
                return o1.getWeekMinFrom().compareTo(o2.getWeekMinFrom());
            }
        });


        List<SchedulingWithShow> actualScheduling = new ArrayList<>();

        for (SchedulingWithShow current : schedulings) {
            //remove if finished
            actualScheduling = actualScheduling.stream().filter(sched -> sched.getWeekMinTo().compareTo(current.getWeekMinFrom()) > 0).collect(Collectors.toList());

            int size = actualScheduling.size();
            current.setOverlap(size);
            actualScheduling.forEach(sched -> sched.setOverlap(size));
            actualScheduling.add(current);
        }

        return schedulings;
    }


    public void generatePdf(Date now, OutputStream outputStream) {
        try {
            List<SchedulingWithShow> schedulings = listSchedulings(now);
            PDDocument document = new PDDocument();
            float xsize = 841.8898F;
            float ysize = 595.27563F;
            PDPage page = new PDPage(new PDRectangle(xsize, ysize));


            document.addPage(page);


            PDSimpleFont font = PDType1Font.HELVETICA;
            EncodingManager encodingManager = new EncodingManager();

            font.setFontEncoding(encodingManager.getEncoding(COSName.WIN_ANSI_ENCODING));


            PDPageContentStream contentStream = new PDPageContentStream(document, page);


            int width = 50;
            contentStream.setLineWidth(1);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.setStrokingColor(Color.BLACK);

            float marginLeft = 40;
            float marginRight = 25;
            float marginTop = 50;

            float dayWidth = (xsize - marginLeft - marginRight) / 7;
            float hourHeight = (ysize - 100) / 24;

            String[] days = new String[]{"Hétfö", "Kedd", "Szerda", "Csütörtök", "Péntek", "Szombat", "Vasárnap"};
            for (int i = 0; i < 7; i++) {
                contentStream.addRect(marginLeft + i * dayWidth - 1, 50, dayWidth, hourHeight * 24);
                contentStream.closeAndStroke();

                contentStream.beginText();
                contentStream.setFont(font, 12);
                String dayName = days[i].toUpperCase();
                contentStream.moveTextPositionByAmount(marginLeft + i * dayWidth - 1 + ((dayWidth - getTextWidth(font, 12, dayName)) / 2), ysize - 35);
                contentStream.drawString(dayName);

                contentStream.endText();
            }


            for (int i = 0; i < 25; i++) {
                //contentStream.setNonStrokingColor(200, 200, 200);
                //contentStream.fillRect(marginLeft - 20, ysize - marginTop - i * hourHeight, 20, 20);
                //contentStream.closeAndStroke();
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.beginText();
                contentStream.setFont(font, 8);
                contentStream.moveTextPositionByAmount(marginLeft - 25, ysize - marginTop - i * hourHeight - 3);
                contentStream.drawString(i + ":00");

                contentStream.endText();
            }

            for (SchedulingWithShow scheduling : schedulings) {
                float xpos = marginLeft + scheduling.getWeekDay() * dayWidth - 1;
                float ypos = marginTop + (24 * 60 - scheduling.getHourFrom() * 60 - scheduling.getMinFrom() - scheduling.getDuration()) / 60f * hourHeight;
                float cellWidth = dayWidth / (scheduling.getOverlap() + 1);
                float cellHight = hourHeight * scheduling.getDuration() / 60;
                float offset = 0;
                if (scheduling.getOverlap() > 0) {


                    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
                    c.set(now.getYear() + 1900, now.getMonth(), now.getDate(), scheduling.getHourFrom(), scheduling.getMinFrom(), 0);
                    int dof = c.get(Calendar.DAY_OF_WEEK) - 2;
                    if (dof < 0) {
                        dof += 7;
                    }
                    c.add(Calendar.DAY_OF_MONTH, -1 * dof + scheduling.getWeekDay());
                    c.set(Calendar.MILLISECOND, 0);


                    int weekNo = (int) Math.floor((c.getTime().getTime() - scheduling.getBase().getTime()) / (1000 * 24 * 60 * 60 * 7));
                    offset = cellWidth * Math.abs(weekNo % scheduling.getWeekType());
                }
                contentStream.setStrokingColor(Color.BLACK);
                if (scheduling.getShowType().equals(ShowType.MUSIC)) {
                    contentStream.setNonStrokingColor(255, 255, 255);
                } else {
                    contentStream.setNonStrokingColor(200, 200, 200);
                }
                contentStream.fillRect(xpos + offset, ypos, cellWidth, cellHight);
                contentStream.addRect(xpos + offset, ypos, cellWidth, cellHight);
                contentStream.closeAndStroke();

                contentStream.beginText();
                String text = scheduling.getShowName().replace('ő', 'ö').replace('ű', 'ü');

                int oneLineFontSize = calculateFontSize(font, cellWidth, cellHight, new String[]{text});

                float titleWidth = getTextWidth(font, oneLineFontSize, text);
                float titleHeight = getTextHeight(font, oneLineFontSize);
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.setFont(font, oneLineFontSize);
                contentStream.moveTextPositionByAmount(xpos + offset + (cellWidth - titleWidth) / 2, ypos + (cellHight - titleHeight) / 2);
                contentStream.drawString(text);
                contentStream.endText();


            }


            contentStream.close();

            document.save(outputStream);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int calculateFontSize(PDSimpleFont font, float cellWidth, float cellHeight, String[] text) throws IOException {
        int fontSize = 4;
        float titleWidth = 0;
        float titleHeight = 0;
        String longestName = Arrays.stream(text).max(Comparator.comparing(String::length)).get();

        while (fontSize < 12 && titleWidth < cellWidth * 0.8 && titleHeight * text.length < cellHeight * 0.9) {
            titleWidth = getTextWidth(font, fontSize, longestName);
            titleHeight = getTextHeight(font, fontSize);
            fontSize++;
        }
        fontSize--;
        return fontSize;
    }

    private float getTextHeight(PDSimpleFont font, int fontSize) {
        return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
    }

    private float getTextWidth(PDSimpleFont font, int fontSize, String text) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }


}

