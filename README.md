# shim

shim is a github public repository for the [Multicore Association SHIM specification](http://www.multicore-association.org/workgroup/shim.php) compliant reference open source software. The most central tool is SHIM Editor, a reference implementation of SHIM XML authoring tool. It also hosts sample SHIM XML and CCF XML files, along with the sample application implementation that shows how to use CCF XML files. Another tool is called SHIM Performance Measurement Plugin, which is an Eclipse plugin that can be used to measure the performance of hardware described in SHIM XML. Altogether, SHIM Editor is used for creating a SHIM XML except for the performance values in the XML, which is measured by the SHIM Performance Measurement Plugin that uses the SHIM XML to generate the performance measurement code, and merging the measurement result back to the XML.

# Directories

[tools](https://github.com/openshim/shim/tree/master/tools) directory includes pre-built executables for forementioned tools. You can just download these and try them. For SHIM Editor, [a simple user manual](https://github.com/openshim/shim/blob/master/tools/shim-editor/docs/SHIMEditor_UsersManual.md) is provided. For the SHIM Performance Measurement Plugin,  [a simillar simple user manual](https://github.com/openshim/shim/tree/master/tools/shim_performance_measurement_plugin/doc) is also provided.

[samples](https://github.com/openshim/shim/tree/master/samples) direcotry includes various SHIM and CCF XML files samples. Please note that these are just samples to provide a feel for how these XML files look like. The accuracy and correctness of the contents are not verified.

[schema](https://github.com/openshim/shim/tree/master/schema) directory includes the .xsd files of SHIM specification. Note that official schema definition is the one dfined in the SHIM specification document provided from the Multicore Association website.

[sources](https://github.com/openshim/shim/tree/master/sources) directory includes multiple eclipse projects containing forementioned tools soruce codes. Please refer to README.txt files in each subdirectory.

