package io.digital.ninjava.jdkdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class JdkdemoApplication {

    @RequiredArgsConstructor
    enum JdkFeature {
        HELP((args) -> {
            log.info("Please provide a valid demo as first argument. Valid arguments are:\n {}",
                    StringUtils.join(values(), "\n "));
        }),
        NPE((args) -> {
            Long a = 1L, b=2L, c=null;
            log.info("{} * {} * {} = {}", a, b, c, a * b * c);
        }),
        JEP_358(NPE),
        VAR((args) -> {
            final var stringArray = args;
            final var num = 1;
            log.info("{} and {}", stringArray, num);
        }),
        JEP_286(VAR),
        SWITCH_EXPRESSION((args) -> {
            var sum = Stream.of(args)
                    // switch expression
                    .mapToInt(arg -> switch (StringUtils.lowerCase(arg)) {
                        case "nul", "nil", "none", "niets" -> 0;
                        case "een", "één", "one" -> 1;
                        case "twee", "two" -> 2;
                        case "drie", "three" -> 3;
                        case "vier", "for" -> 4;
                        case "vijf", "five" -> 5;
                        case "zes", "six" -> 6;
                        case "zeven", "zeuven", "seven" -> 7;
                        case "acht", "eight" -> {
                            log.info("In a switch expression, I need a `yield` statement if I create a block");
                            yield 8;
                        }
                        case "negen", "nine" -> 9;
                        default -> throw new IllegalArgumentException(String.format("\"%s\" is not a parseable number", arg));
                    })
                    .peek(asNum -> log.info("as Number: {}", asNum))
                    .sum();
            log.info("Sum of {} (parsed as numbers) is: {}", StringUtils.join(args, ", "), sum);
        }),
        JEP_361(SWITCH_EXPRESSION),
        TEXT_BLOCKS(args -> {
            var json = """
                     {
                       "feature" : {
                          "JEP_378": "nice"
                       }
                     }
                    """;
            var oldskool = "{\n" +
                    "  \"feature\" : {\n" +
                    "     \"JEP_378\" : \"would be nice!\"\n" +
                    "  }\n" +
                    "}\n"
                    ;
            log.info("Using JEP 378: {}", json);
            log.info("Without JEP 378: {}", oldskool);
        }), JEP_378(TEXT_BLOCKS),
        PATTERN_MATCHING(new PatternMatching()),
        JEP_394(PATTERN_MATCHING);

        private final Demo demo;
        JdkFeature(JdkFeature alias) {
            this(alias.demo);
        }

        public void go(String... args) {
            demo.go(args);
        }

        public static JdkFeature of(String val) {
            return Stream.of(values())
                    .filter(candidate -> StringUtils.equalsIgnoreCase(candidate.name(), val))
                    .findFirst()
                    .orElse(HELP);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            JdkFeature.of(args[0]).go(Arrays.copyOfRange(args, 1, args.length));
        } else {
            JdkFeature.HELP.go();
        }
    }

}
