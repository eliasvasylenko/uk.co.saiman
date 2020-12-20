{ pkgs ? import <nixpkgs> {
  
  overlays = [(self: super:
    with pkgs;
    let
      jdk = super.openjdk14.override { enableJavaFX = false; };

      platform_major = "4";
      platform_minor = "17";
      year = "2020";
      month = "09";
    in {
      jdk = jdk;
      jvm = jdk;
      
      eclipse-rcp = eclipses.buildEclipse {
        name = "eclipse-rcp-${platform_major}.${platform_minor}";
        description = "Eclipse IDE for RCP and RAP Developers";
        src =
          fetchurl {
            url = "https://www.eclipse.org/downloads/download.php?r=1&nf=1&file=/technology/epp/downloads/release/${year}-${month}/R/eclipse-rcp-${year}-${month}-R-linux-gtk-x86_64.tar.gz";
            sha512 = "216g805fgpb7vwbgk36xkf467vijvaa3x0gk4d94pi92d9wdh5dph6wjjr4ka9rvg0pzxxr8y3sgbfqqzqpc82bgiz5qqr93sprh9bj";
          };
      };
    }
  )];
} }:

with pkgs;
mkShell {
  name = "uk.co.saiman-env";
  buildInputs = [
    jdk
    gradle
    glib-networking
    (with eclipses; eclipseWithPlugins {
      eclipse = eclipse-rcp;
      jvmArgs = [ "-Xmx4096m" ];
      plugins = [
        plugins.color-theme
        (plugins.buildEclipseUpdateSite rec {
          name = "Bndtools";
          src = fetchzip {
            stripRoot = false;
            url = "https://bndtools.jfrog.io/bndtools/update-latest/libs-release-local/org/bndtools/org.bndtools.p2/5.2.0/org.bndtools.p2-5.2.0.jar#bndtools.zip";
            sha256 = "1lkqwxsyrp52cv4nfsp6gsh96jkpvbl4b0lk0dl35zg9q00wdwv5";
          };
        })
      ];
    }) gnome3.adwaita-icon-theme
  ];
  shellHook = ''
    export XDG_DATA_DIRS=$XDG_DATA_DIRS:${gnome3.adwaita-icon-theme}/share
  '';
}
