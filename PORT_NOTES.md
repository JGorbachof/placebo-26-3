# Placebo unofficial 26.3 port

Original: Shadows_of_Fire (Brennan Ward); https://github.com/Shadows-of-Fire/Placebo
Upstream revision: 8fcfa1e6e947e8f0f12044ad31ce4bef2145982a (26.1 branch, version 10.0.2).
Source license: MIT; original copyright retained. Wing assets are committed upstream by Brennan Ward; no separate asset license was found. Root MIT license retained; inherited Forge LGPL-2.1 and Guava Apache-2.0 notices and texts are included in LICENSES.
Target: Minecraft 26.3, NeoForge 26.3.0.118, Java 25, ModDevGradle 2.0.148.

The entire production source tree is retained. No dependent mod port was started.
- Replaced removed PotionBrewing.Mix with JsonMix.Mix; preserved legacy JSON keys and converted registered mixes into native brewing recipes before recipe index finalization. Client accessor now returns RecipeAccess. Server-side assembly preserves input potion contents or container components. Native recipe priority, conflicting legacy mixes, nonstandard containers, multiplayer recipe sync and actual reload behavior require further integration coverage.
- Migrated data-generation helpers to separate world and reloadable registry layers; retained conditions. LegacyRecipeProvider implements DataProvider and emits shaped/shapeless recipes directly with codecs, because vanilla removed RecipeProvider.Runner. Direct legacy recipe emission/decoding passed; full GatherDataEvent and registry bootstrap integration have not been exercised.
- Updated registration helpers for changed vanilla Feature and structure processor APIs, inventory prediction, input, HUD, tooltips, and wing rendering. Removed obsolete deprecated immediate-mode text render overload (MultiBufferSource removed); retained GuiGraphicsExtractor alternative.
- Retained mod ID, package names, dynamic registry/holder implementation and all existing mixin classes; added RecipeManagerMixin. Removed upstream publishing tasks from the active build.

Verification: ../placebo-build-pass2.log BUILD SUCCESSFUL (initial 53 errors resolved; two deprecation warnings). ../placebo-qc/qc-pass1.log and qc-pass2.log each report 2/2 required GameTests passed. Custom assertions validate dynamic JSON registration, holder binding, potion/container recipes and reagent index, vanilla brewing, item cache reuse/invalidation; extended test validates shaped/shapeless output and decode plus metadata retention under HashCache. Both run against the actual packaged JAR in the isolated harness.

Status: server-tested candidate, not full runtime acceptance. Client rendering, multiplayer, real datapack reload, full datagen bootstrap, downstream Apothic integration, remain unverified. Release authorized by owner; full modpack integration deferred.
