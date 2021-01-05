# reticle_angle_chooser
Reticle angle chooser is a GUI component for choosing angles, degrees, directions, etc.  This one is a fairly simple component.  The user just drags the grip into what every direction they want.  I found this best combined with a label or textbox to display the angle value as the user drags the grip.

The AngleObserver class is a like a listener which is called during every drag operation.  This makes it easy to pair the component with labels and textboxes.

The component is based on the java swing JComponent class, so it's easy to add to a JFrame or JDialog.

The ReticleAngleChooserBuilder class cn be modified for more options.
