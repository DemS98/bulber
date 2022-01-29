# bulber

**bulber** is a Java Swing app for controlling a TP-Link Smart Bulb.

It supports vocal and GUI commands.

# Table of contents

- [bulber](#bulber)
- [Table of contents](#table-of-contents)
  - [Requirements](#requirements)
  - [Running the app](#running-the-app)
  - [Usage](#usage)
    - [Startup](#startup)
    - [Control Panel](#control-panel)
      - [Vocal commands](#vocal-commands)
        - [Color](#color)
        - [Brightness](#brightness)
        - [Temperature](#temperature)
        - [Numeric](#numeric)
        - [On](#on)
        - [Off](#off)
        - [Transition](#transition)
      - [GUI](#gui)
        - [GUI and Vocal commands](#gui-and-vocal-commands)
        - [Change Language](#change-language)
          - [Italian](#italian)
        - [Other GUI functions](#other-gui-functions)
          - [Note](#note)
- [Author](#author)

## Requirements

* JRE/JDK 8+
* TP-Link Smart Bulb (Kasa) connected to the WLAN

A list of supported bulbs can be found [here](https://github.com/python-kasa/python-kasa).

## Running the app

Clone this repo and run `mvn clean package` on the directory where the *pom.xml* is located.\
Then simply run `java -jar bulber-1.0.jar`.

## Usage

The usage instructions.

### Startup

During startup, **bulber** searches for all smart bulbs in the local network.\
When the search is done, this window is shown:

![Startup window](screenshots/bulber_start_window.png?raw=true "startup window")

You can double-click on a table element, or select it and hit *Start*, to use the specific bulb.

If no bulb was found, you can click on the *Refresh* button to repeat the search.\
If it doesn't work, try to disconnect and reconnect to your local network, where the bulb is connected,
and click on the *Refresh* button again.

### Control Panel

After loading, this window is shown:

![Control Panel window](screenshots/bulber_control_panel.png?raw=true "control panel window")

In the **Microphone** section, you can press the mic icon (without releasing) to activate the vocal command function.\
The check-box *Keep on* actives vocal commands without the need of the user to keep the mic button pressed.

The blue screen shows vocal commands done by the user, if they are valid, in LCD style; the slider below
changes the speed of the text shown in the display.

#### Vocal commands

These are the supported vocal commands:

* [color](#color)
* [brightness](#brightness)
* [temperature](#temperature)
* [numeric](#numeric)
* [on](#on)
* [off](#off)

##### Color

Change the color of the bulb.

Supported values are:

* black
* blue
* magenta
* cyan
* gray
* green
* orange
* pink
* red
* white
* yellow

[Here](audio_samples/color_blue.wav "color example") the audio example.

##### Brightness

Change the brightness of the bulb.

Values in range `[0,100]` are supported.

[Here](audio_samples/brightness_85.wav "brightness example") the audio example.

##### Temperature

Changes the temperature, in Kelvin, of the bulb.

Supported by a subset of bulbs, range variates from bulb to bulb.

[Here](audio_samples/temperature_2550.wav "temperature example") the audio example.

##### Numeric

Change the color of the bulb, specified by its red, green and blue (RGB) values.

This command is ideal for selecting a specific color, not covered by the [color](#color) case.

The three numerical values are in the RGB range `[0,255]` and must be separated by the word *dot*.

[Here](audio_samples/numeric_0_dot_255_dot_0.wav "numeric example") the audio example.

##### On

Start the bulb after it's been shutdown with [off](#off).

You cannot start a bulb when the light switch is off.

##### Off

Shutdown the bulb.

Can be reopened with [on](#on).

##### Transition

The parameter **transition** can be appended to a vocal command.

Through this, you can control the transition between the previous and new state of the bulb. For example,
specifying a transition of 5 seconds at the `color blue` command will cause the bulb to switch to that color
in the span of 5 seconds; so, less the value, less the time to switch to color blue.

The value can be in milliseconds (plain number) or seconds (*seconds* appended to the number value).
Range is `[0,15000]` for milliseconds and `[0,15]` for seconds.

[Here](audio_samples/temperature_2550_transition_3780.wav "transition millis example") the milliseconds audio example.

[Here](audio_samples/temperature_2550_transition_2_seconds.wav "transition seconds example") the seconds audio example.

#### GUI

The GUI is pretty simple:

* Color Picker: change the color of the bulb. You can also select an RGB value from the relative tab. ([Note](#note))
* Brightness: change the bulb color brightness
* Temperature: change the bulb temperature (not available in all bulbs)
* Transition: control the transition (both in seconds and in milliseconds) for the previous commands

To confirm the change, you need to press the *Confirm* button of the command section.

##### GUI and Vocal commands

When a vocal command is fired, the respective GUI section is updated.\
For example, a *temperature 8000 transition 3 seconds* vocal command causes the temperature section
to change to *8000K* (both the text field and the slider) and the transition section to *3s* (both the text field and the slider).\
The inverse is not valid (e.g. if the transition is set to 3s in the GUI, a vocal command is not affected by it).

##### Change Language

You can select a language flag at the top right of the panel to change language.\
The default language is automatically detected from the JVM.\
For now, only english and italian are supported.

###### Italian

The italian settings for vocal commands are the followings:

* [colore](#color)
  * nero
  * blu
  * magenta
  * ciano
  * grigio
  * verde
  * arancione
  * rosa
  * rosso
  * bianco
  * giallo
* [potenza](#brightness)
* [temperatura](#temperature)
* [numerico](#numeric)
  * Separator is the word *punto*
* [apri](#on)
* [chiudi](#off)

Transition is *transizione* with seconds as *secondo | secondi*

##### Other GUI functions

* The <img src="src/main/resources/icons/info.png" width="20" height="20"> button shows information about the bulb
* The <img src="src/main/resources/icons/back.png" width="20" height="20"> button returns to the [Startup](#startup) window

###### Note

The color picker panel language cannot be changed due to a bug of the Swing framework.

# Author

Alessandro Chiariello (dems98)