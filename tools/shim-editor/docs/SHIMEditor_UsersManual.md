# How to use SHIM Editor

1. [Run SHIM Editor](#1_run)
2. [Create SHIM XML](#2_create)
  - [1st page: Setting ComponentSetInfomation](#2_1_page)
  - [2nd page: Edit Component Structure](#2_2_page)
  - [3rd page: Setting AddressSpace Infomation](#2_3_page)
  - [4th page: Setting AddressSpace](#2_4_page)
  - [5th page: Setting Communication Set](#2_5_page)
  - [6th page: Cache Infomation](#2_6_page)
  - [7th page: Setting Infomation of Base AccessType](#2_7_page)
3. [Load SHIM XML](#3_load)
4. [Edit SHIM XML](#4_edit)
  - [Add Child Elements](#4_1_add)
  - [Delete Item](#4_2_delete)
  - [Edit element's parameters](#4_3_edit)
  - [Copy & Paste Item](#4_4_copypaste)
  - [Re-number nodes](#4_5_renumber)
  - [Re-Make AddressSpaceSet(or CommunicationSet)](#4_6_remake)

## <a name="1_run">1.Run SHIM Editor</a>
In gui environment, if the java executable is in your PATH, double-click on the 'shimedit_${OS}.jar' and run the SHIM editor.
If you want to run the SHIM Editor from the command line, execute the following command in terminal.

> java -jar ${INSTALLDIR}/shimedit_${OS}.jar


## <a name="2_create">2.Create SHIM XML</a>

SHIM Editor starts and show the Editor page.
Click on *New* and the "Create New SHIM Data Wizard" starts.
Refer to an after section for what configure by each page of Wizards.

<center>
<img src="pic/002_001.png" width="500" height="300">
**figure1. Editor page**
</center>


#### <a name="2_1_page">1st page: Setting ComponentSetInfomation</a>

You should input some basic infomation for new SHIM XML for creating initial component set structure.
When you configure *MasterComponent*, select a tab of *MasterComponent*. In case of configuring *SlaveComponent* or *ComponentSet*, like, select a tab of *SlaveComponent* or *ComponentSet*.

<center>
<img border="0" src="pic/002_002.png" width="500" height="300">
**figure2. Setting ComponentSetInfomation**
</center>


#### <a name="2_2_page">2nd page: Edit Component Structure</a>

The 2nd page shows the structure of *ComponentSet* that is created based on the settings you inputted at the previous page.

<center>
<img src="pic/002_003.png" width="500" height="300">
**figure3. Edit Component Structure**
</center>

If you'd like to add a child component or to delete components, right-click on the target components and select *Add Child ～* or *Delete Item*.

<center>
<img src="pic/002_004.png" width="500" height="300">
**figure4. Edit Component Structure's menu items**
</center>


#### <a name="2_3_page">3rd page: Setting AddressSpace Infomation</a>

At this page, input the number of the element such as *AddressSpace* and *SubSpace* that you'd like to create.

<center>
<img src="pic/002_005.png" width="500" height="300">
**figure5. Setting AddressSpace Infomation**
</center>

#### <a name="2_4_page">4th page: Setting AddressSpace</a>

The 4th page shows AddressSpaces and SubSpaces.
You can edit the infomation of those elements.
If you'd like to add a new SubSpace or to delete AddressSpace, SubSpace, right-click on the target components and select *Add Child ～* or *Delete Item*.

<center>
<img src="pic/002_006.png" width="500" height="300">
**figure6. Setting AddressSpace**
</center>


#### <a name="2_5_page">5th page: Setting Communication Set</a>

Select Communication Set you'd like to create, and configure them.
<center>
<img src="pic/002_007.png" width="500" height="300">
**figure7. Setting Communication Set**
</center>


#### <a name="2_6_page">6th page: Cache Infomation</a>

Select *Cache Type*, and configure the cache.
<center>
<img src="pic/002_008.png" width="500" height="300">
**figure8. Cache Infomation**
</center>

If you select *Data and Instruction* at the *Cache Type* selection, you should configure *Data Cache* and *Instruction Cache*.
<center>
<img src="pic/002_009.png" width="500" height="300">
**figure9. Cache Infomation (Data and Instruction)**
</center>


#### <a name="2_7_page">7th page: Setting Infomation of Base AccessType</a>

The 7th page is the final page.You can configure *AccessType* at this page.
The number of *AccessType* is determined by the following formula.

- [the number of *RWType*] × [the number of *AccessByteSize*]

<center>
<img src="pic/002_010.png" width="500" height="300">
**figure10. Setting Infomation of Base AccessType**
</center>


## <a name="3_load">3.Load SHIM XML</a>

If you have a SHIM XML file, you open and edit it.
Click on Open, and select SHIM XML file.

<center>
<img src="pic/003_001.png" width="500" height="300">
**figure11. Load SHIM XML**
</center>

<center>
<img src="pic/003_002.png" width="500" height="300">
**figure12. Load SHIM XML, FileDialog**
</center>


## <a name="4_edit">4.Edit SHIM XML</a>

If you create a new SHIM XML or open a SHIM XML file, SHIM Editor shows some infomation of SHIM XML.
Introduce about the following typical control by an after section.
- Add Child Elements
- Delete item
- Copy Item
- Paste Item
- Re-number nodes
- Edit element's infomation

##### <a name="4_1_add">Add Child Elements</a>

To add an element, you must know which element is its parent element.
Right-click on the element to be belonged in the TreeViewer, you can select *Add Child Element*.

For example, add child *ComponentSet* to the *ComponentSet* in this section.

If you'd like to edit the structure of component, select *Components* tab.
Right-click on the parent *ComponentSet*, select *Add Child ComponentSet*.

<center>
<img src="pic/004_002.png" width="500" height="300">
**figure13. Add Child Component**

<img src="pic/004_003.png" width="500" height="300">
**figure14. After Add Child Component**
</center>

##### <a name="4_2_delete">Delete Item</a>

If you'd like to delete an element, right-click on the target element in Treeviewer and select *Delete Item*.
The point to notice is that the required element cannot delete.If right-click on a required element, *Delete Item* is not shown.

<center>
<img src="pic/004_004.png" width="500" height="300">
**figure15. example that _Delete Item_ is shown**

<img src="pic/004_005.png" width="500" height="300">
**figure16. example that _Delete Item_ is not shown**
</center>

##### <a name="4_3_edit">Edit Element's Paramerer</a>

There are several ways to edit element's parameter(ex. attribute, child elements).

The simplest way is that you select a target element in TreeViewer and edit displayed parameters.
You can adjust parameters and then left-click on the *Apply* button to make them effective.

<center>
<img src="pic/004_006.png" width="500" height="300">
**figure17. Edit an element from an element's input panel**
</center>

When you select the parent element in TreeViewer, there are cases where you can edit the parameters of the child element.
For example, when you select an *AddressSpace* element, you can edit parameters of the child *SubSpace* elements.

<center>
<img src="pic/004_007.png" width="500" height="300">
**figure18. Edit an element from a parent element's input panel**
</center>

If you'd like to edit an element with viewing other parameters of the same element, you should use *ElementTable*.
When you select an element in TreeViewer and left-click on *ElementTable*, SHIM Editor shows *ElementTable* that shows all parameters of the same elements.

<center>
<img src="pic/004_008.png" width="500" height="300">
**figure19. Before left-click on _ElementTable_**

<img src="pic/004_009.png" width="500" height="300">
**figure20. After left-click on _ElementTable_**
</center>

You can edit table data in this view.
If you want to leave *ElementTable* view, left-click on *ElementTable* again.

##### <a name="4_4_copypaste">Copy & Paste Item</a>

SHIM Editor supports copy and paste.
To copy the item, you select the item, right-click on it and select *Copy Item(s)*.

<center>
<img src="pic/004_010.png" width="500" height="300">
**figure21. Copy Item(s)**
</center>

To paste the item, you select the item that is the parent element of the copy item, right-click on it and select *Paste Item*.

<center>
<img src="pic/004_011.png" width="500" height="300">
**figure22. Paste Item(s)**
</center>

You can paste the item according to the schema of SHIM XML.
So, if you copy the item that *maxOccurs="1"* and paste it, the original item of the same element is overwritten.

<center>
<img src="pic/004_012.png" width="500" height="300">
**figure23. Before Paste item and Overwrite**

<img src="pic/004_013.png" width="500" height="300">
**figure24. After Paste item and Overwrite**
</center>

##### <a name="4_5_renumber">Re-number nodes</a>

After adding child elements, deleting child elements and so on, you want to assign automatically component numbers.
In this case, leftt-click on *Re-number nodes* button.

<center>
<img src="pic/004_014.png" width="500" height="300">
**figure25. Before _Re-number nodes_**

<img src="pic/004_015.png" width="500" height="300">
**figure26. After _Re-number nodes_**
</center>

##### <a name="4_6_remake">Re-Make AddressSpaceSet(or CommunicationSet)</a>

SHIM Editor does not create automatically new CommunicationSet or MasterSlaveBindingSet after *Add Child Master(or Slave)Component* and *Paste the Master(or Slave)Component*, because doesn't know whether those are needed.
If you want to create all combinations of AddressSpaceSet or CommunicationSet, left-click on *Re-Make AddressSpaceSet(or CommunicationSet)*.

<center>
<img src="pic/004_016.png" width="500" height="300">
**figure27. Re-Make AddressSpaceSet(or CommunicationSet)**
</center>

Re-Make AddressSpaceSet Wizard consists of pages *Create New SHIM Data Wizard*'s' 3rd page and 4th page.
Re-Make CommunicationSet Wizard cosists of only *Create New SHIM Data Wizard*'s' 5th page.
Refer to below for the input contents of each page.
- [3rd page: Setting AddressSpace Infomation](#2_3_page)
- [4th page: Setting AddressSpace](#2_4_page)
- [5th page: Setting Communication Set](#2_5_page)

<br>
<br>
