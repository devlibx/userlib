package org.exampl.drools;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <pre>
 *          <dependency>
 *             <groupId>org.kie</groupId>
 *             <artifactId>kie-ci</artifactId>
 *             <version>7.1.0.Beta2</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.drools</groupId>
 *             <artifactId>drools-decisiontables</artifactId>
 *             <version>7.1.0.Beta2</version>
 *         </dependency>
 */

public class Application {
    public static void main(String[] args) throws IOException {

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        // Read the XLS file with the conditions
        Resource dt = ResourceFactory.newFileResource(new File("/Users/harishbohara/Downloads/rule (9).xlsx"));

        // Option 1 -> Convert XLS to DRL file (so you can save it in DB)
        // Write it to some file and load it in file system
        DecisionTableProviderImpl decisionTableProvider = new DecisionTableProviderImpl();
        String drl = decisionTableProvider.loadFromResource(dt, null);
        FileUtils.writeStringToFile(new File("/tmp/a.drl"), drl, StandardCharsets.UTF_8);
        kfs.write(ResourceFactory.newFileResource("/tmp/a.drl"));
        System.out.println(drl);

        // Option 2 -> Directly read this XLX file
        // KieFileSystem kfs = ks.newKieFileSystem().write(dt);


        // Build all and print any errors
        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        // Build a session
        KieRepository kr = ks.getRepository();
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        KieSession kieSession = kContainer.newKieSession();

        // This is the real code - here we are creating a customer object and running all rules
        Customer customer = Customer.builder().type(Customer.CustomerType.INDIVIDUAL).years(20).build();
        Hacker hacker = Hacker.builder().score(20).build();
        kieSession.insert(customer);
        kieSession.insert(hacker);
        kieSession.fireAllRules();

        // Finally Result - we should have got the discount
        System.out.println(customer.getDiscount());
    }
}


