package at.bal.views.importexport;

import at.bal.model.GreenTracker;
import at.bal.model.GreenTrackerException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@PageTitle("Import / Export")
@Route("import-export")
@Menu(order = 3, icon = LineAwesomeIconUrl.FILE_UPLOAD_SOLID)
public class ImportExportView extends VerticalLayout {

    private static final Path CSV_PFAD = Path.of("data", "greentracker.csv");

    public ImportExportView() {
        setSpacing(true);
        setPadding(true);

        add(new H2("Import / Export"));
        add(new Paragraph("Lade deine Verbrauchsdaten als CSV herunter oder importiere eine vorhandene CSV-Datei."));

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(exportButton(), importUpload());
        add(buttons);
    }

    private Anchor exportButton() {
        DownloadHandler handler = DownloadHandler.forFile(CSV_PFAD.toFile());

        Anchor anchor = new Anchor(handler, "");
        anchor.add(new Button("Export (CSV herunterladen)", new Icon(VaadinIcon.DOWNLOAD)));
        return anchor;
    }

    private Upload importUpload() {
        UploadHandler handler = UploadHandler.inMemory((metadata, data) -> {
            try {
                Files.createDirectories(CSV_PFAD.getParent());
                Files.write(CSV_PFAD, data);

                GreenTracker gt = new GreenTracker("Greentracker");
                gt.readVerbraucheFromCsv();
                Notification.show("Import erfolgreich: " + gt.getVerbrauche().size() + " Verbräuche");
            } catch (IOException | GreenTrackerException e) {
                Notification.show("Import fehlgeschlagen: " + e.getMessage());
            }
        });

        Upload upload = new Upload(handler);
        upload.setAcceptedFileTypes(".csv", "text/csv");
        upload.setMaxFiles(1);
        return upload;
    }
}