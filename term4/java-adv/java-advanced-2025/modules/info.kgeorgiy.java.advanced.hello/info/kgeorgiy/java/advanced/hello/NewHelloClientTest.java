package info.kgeorgiy.java.advanced.hello;

import org.junit.jupiter.api.Test;

import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Full tests for {@link HelloClient}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NewHelloClientTest extends HelloClientTest {
    public NewHelloClientTest() {
    }

    @Test
    public void test11_multiPrefix() throws SocketException {
        final List<List<String>> templates = IntStream.range(0, 3)
                .mapToObj(i -> templates(prefix(), i * 3, (i + 1) * 3))
                .toList();

        testMulti(templates);
    }

    @Test
    public void test12_samePrefix() throws SocketException {
        testMulti(Collections.nCopies(3, Collections.nCopies(3, prefix() + "$")));
    }

    @Test
    public void test13_randomPrefix() throws SocketException {
        final List<String> templates = List.of(
                "swicvachiatHod_vibEv$virOjPoibMi_hdradFec",
                "insEwUkoovvowJ_edein$IvMawyeeddya_psUpNid",
                "ildinowyudEbud_Ifmam$yievSypZamye_vHivBow",
                "namIgniunonecy_irtOd$GrevCeicOnlo_nOlpEed",
                "RojTidafWiVajr_awgor$RyfsAymjoydU_gbyggid",
                "jimHathalgyoum_Ufrax$FierIrpUvper_jOoryeg",
                "afCunAkhevewEf_teurb$hibitdojCogm_ofarbos",
                "anCiCyfropmouk_Efeed$traurtutNajb_yRawAuk",
                "FritOgbokVaHecdim_ve$SeavinrenCunytUc_Ofc",
                "candUncyruwaft_yerux$TrecApEcAjId_nocboaf"
        );
        testMulti(IntStream.range(0, 3)
                .mapToObj(i -> IntStream.range(0, 3).mapToObj(j -> templates.get(i * 3 + j)).toList())
                .toList());
    }

    private void testMulti(final List<List<String>> templates) throws SocketException {
        final List<Server<Integer>> serverPorts = IntStream.range(0, templates.size())
                .mapToObj(i -> new Server<>(templates.get(i), i + PORT))
                .toList();
        final List<NewHelloClient.Request> requests = serverPorts.stream()
                .flatMap(server -> server.templates().stream()
                        .map(template -> new NewHelloClient.Request("localhost", server.value(), template)))
                .toList();
        test(10, 0.5, serverPorts, (NewHelloClient client, Integer threads) -> client.newRun(requests, threads));
    }
}
