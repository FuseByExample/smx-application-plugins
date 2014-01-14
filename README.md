This projects contains a sample integration application that demostrates how to extend the integration capabilities of a core process at runtime(!) via hot-deployable application level plugins.

This sample is based around a fictional flight booking application. The core process takes flight details which were previously gathered, and if there exists a plugin for the requested airline it accept payment for the ticket and calls out to the airline's back-end to place the booking. 

Airline plugins are OSGi bundles that use [Camel](http://camel.apache.org/) for the integration to their respective systems.

Project layout
==============
The Maven projects contained within are as follows:

* `flights-features` - Contains an XML features file used to install the rest of the bundles.
* `flights-booking` - Contains a bundle that exposes a core booking process through a REST endpoint.
* `flights-booking-spi` - Defines an interface through which plugins can register their availability in the OSGi service registry.
* `flights-plugin-german-airline` - A plugin into the booking process for a ficticious German airline that accepts bookings for tickets starting with "DE".
* `flights-plugin-irish-airline` - A plugin into the booking process for a ficticious Irish airline that accepts bookings for tickets starting with "IE".

There is also an additional parent project `camel-bundle` that simplifies the Maven project configuration.

Prerequisites
=============
Set up JBoss Fuse by downloading the latest 6.1.0 version from [Red Hat](https://access.redhat.com/jbossnetwork/).

Ensure that Maven is set up on your system. 

Installation
============
Download this project and run

	smx-bootstraps> mvn clean install

Start JBoss Fuse

	$JBOSS_FUSE_HOME> bin/fuse
	
          _ ____                  ______
         | |  _ \                |  ____|
         | | |_) | ___  ___ ___  | |__ _   _ ___  ___
     _   | |  _ < / _ \/ __/ __| |  __| | | / __|/ _ \
    | |__| | |_) | (_) \__ \__ \ | |  | |_| \__ \  __/
     \____/|____/ \___/|___/___/ |_|   \__,_|___/\___|

      JBoss Fuse (6.1.0.redhat-312)
      http://www.redhat.com/products/jbossenterprisemiddleware/fuse/

    Hit '<tab>' for a list of available commands
    and '[cmd] --help' for help on a specific command.


Install the features file from your local Maven repo into the known collection of features:

	JBossFuse:karaf@root> features:addurl mvn:com.fusesource.examples/flights-features/1.0-SNAPSHOT/xml/features

You can now check that the features defined in that file are available for installation:

	JBossFuse:karaf@root> features:list | grep flights
	[uninstalled] [1.0                 ] flights-booking                      smx-application-plugins           
	[uninstalled] [1.0                 ] flights-irish-airline                smx-application-plugins           
	[uninstalled] [1.0                 ] flights-german-airline               smx-application-plugins

_NP: It's often a good idea to prefix all of your features and bundles with a known string, such as `flights` in this case, so you can easily find them via the grep command in the various listings._

Install the core booking system's OSGi bundles by installing the `flights-booking` feature

	JBossFuse:karaf@root> features:install flights-booking
	JBossFuse:karaf@root> list | grep flights
	[ 254] [Active     ] [            ] [Started] [   60] flights-booking (1.0.0.SNAPSHOT)
	[ 255] [Active     ] [            ] [       ] [   60] flights-booking-spi (1.0.0.SNAPSHOT)

Check that bookings for an Irish Airlines are unsupported by hitting the following from your web browser:

	http://localhost:9191/booking?flightNumber=IE943

This should return:

	Unable to book flight for IE943 - unsupported airline

Now install the feature which enables integration with Irish Airlines:

	JBossFuse:karaf@root> features:install flights-irish-airline 
	JBossFuse:karaf@root> list | grep flights
	[ 254] [Active     ] [            ] [Started] [   60] flights-booking (1.0.0.SNAPSHOT)
	[ 255] [Active     ] [            ] [       ] [   60] flights-booking-spi (1.0.0.SNAPSHOT)
	[ 256] [Active     ] [            ] [Started] [   60] flights-plugin-irish-airline (1.0.0.SNAPSHOT)

Refresh the URL in your browser, you should now see that as the integration service has been made available, the core process will now take payment for the flight and invoke the airline's back-end.

	Taking payment for IE943;Irish Airline processed booking 

Repeat again for a flight number starting with "DE" and installing the `flights-german-airline` feature.

Voila! Hot deployable integrations as plugins.

Next steps
==========

Take a look at a plugin's SpringDM config in `src/main/resources/META-INF/spring`. Note how the plugin exposes its Camel endpoint details to the OSGi service registry.

Check out the Spring config in the `flights-booking` project. Note how the interfaces registered by the plugins are accessed as a Set of `BookingProcessor` interfaces, and read by the `BookingProcessorRegistry` to provide an object that is able to be used in Camel expressions to influence the routing of the core booking process.
