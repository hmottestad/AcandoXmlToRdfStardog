package no.acando;

import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import no.acando.xmltordf.Builder;
import org.apache.commons.io.IOUtils;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

/**
 * Created by havardottestad on 13/12/16.
 */
@RestController
public class Web {

    Random r = new Random();

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @CrossOrigin("*")
    public
    @ResponseBody
    String handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {

        InputStream inputStream = file.getInputStream();
//        String s = IOUtils.toString(inputStream, "utf-8");
//        System.out.println(s);

        String database = "DB_"+r.nextInt(1000) + "_______" + UUID.randomUUID();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Builder.getAdvancedBuilderStream()
            .setBaseNamespace("http://example.com/", Builder.AppliesTo.bothElementsAndAttributes)
            .uuidBasedIdInsteadOfBlankNodes("http://example.com/")
            .build()
            .convertToStream(inputStream,byteArrayOutputStream);



        AdminConnectionConfiguration adminConnectionConfiguration = AdminConnectionConfiguration
            .toServer("http://128.199.32.62:5820")
            .credentials("admin", "acando");

        try (AdminConnection connect = adminConnectionConfiguration.connect()) {

            connect.disk(database)
                .create();
        }

        ConnectionConfiguration credentials = ConnectionConfiguration
            .to(database)
            .server("http://128.199.32.62:5820")
            .reasoning(true)
            .credentials("admin", "acando");

        try (Connection connect = credentials.connect()) {

            connect.begin();
            connect.add().io().format(RDFFormat.TURTLE).serverSide().stream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            connect.commit();

        }

        return "<a href=\"http://128.199.32.62:5820/"+database+"#!/schema\">Trykk her<a/><p>Databasen heter: "+database+"</p>";
    }


}