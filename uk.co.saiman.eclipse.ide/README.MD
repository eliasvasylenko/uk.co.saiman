# MSApex IDE Plugin

This project collects the bundles required for the MSApex IDE feature. The IDE feature is a plugin for Eclipse which provides model editors for cells and tables.

## Installation

Currently the plugin is not packaged and published via any p2 repository, so it must be manually bootstrapped into the running IDE. To accomplish this in Eclipse, follow these steps:

1. Install PDE tooling into Eclipse IDE.

1. Ensure the workspace is fully built.

1. Open `feature.xml` in the feature manifest editor.

1. Navigate to the `Included Plug-ins` tab to resolve the included components
  
  1. Click `Versions...`
  
  1. Select `Copy version from plug-in, feature and fragment manifests`
  
  1. Hit `Finish`

1. Navigate to the `Overview` tab to export to a local p2 site

  1. Hit `export Wizard`
  
  1. Select an appropriate output directory
  
  1. Hit `Finish`

1. Navigate to `Help` -> `Install new software ...`, and install the feature from the output directory chosen above.
