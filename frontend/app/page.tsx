import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Car, ShieldCheck, Zap, Clock } from "lucide-react";

export default function HomePage() {
  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="py-20 px-4 md:py-32 bg-gradient-to-b from-background to-muted/20">
        <div className="container mx-auto text-center space-y-6">
          <h1 className="text-4xl md:text-6xl font-bold tracking-tight">
            Find Your Perfect Vehicle
          </h1>
          <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
            Browse our extensive collection of vehicles, customize them to your preferences,
            and purchase online with confidence.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center pt-4">
            <Button size="lg" asChild>
              <Link href="/vehicles">Browse Vehicles</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link href="/auth/register">Get Started</Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 px-4">
        <div className="container mx-auto">
          <h2 className="text-3xl font-bold text-center mb-12">
            Why Choose BennyCar?
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="flex flex-col items-center text-center space-y-4">
              <div className="h-16 w-16 rounded-full bg-primary/10 flex items-center justify-center">
                <Car className="h-8 w-8 text-primary" />
              </div>
              <h3 className="font-semibold text-lg">Wide Selection</h3>
              <p className="text-sm text-muted-foreground">
                Choose from hundreds of vehicles from top brands worldwide
              </p>
            </div>

            <div className="flex flex-col items-center text-center space-y-4">
              <div className="h-16 w-16 rounded-full bg-primary/10 flex items-center justify-center">
                <Zap className="h-8 w-8 text-primary" />
              </div>
              <h3 className="font-semibold text-lg">Easy Customization</h3>
              <p className="text-sm text-muted-foreground">
                Personalize your vehicle with our intuitive configuration tool
              </p>
            </div>

            <div className="flex flex-col items-center text-center space-y-4">
              <div className="h-16 w-16 rounded-full bg-primary/10 flex items-center justify-center">
                <ShieldCheck className="h-8 w-8 text-primary" />
              </div>
              <h3 className="font-semibold text-lg">Secure Transactions</h3>
              <p className="text-sm text-muted-foreground">
                Your purchase is protected with industry-leading security
              </p>
            </div>

            <div className="flex flex-col items-center text-center space-y-4">
              <div className="h-16 w-16 rounded-full bg-primary/10 flex items-center justify-center">
                <Clock className="h-8 w-8 text-primary" />
              </div>
              <h3 className="font-semibold text-lg">Fast Delivery</h3>
              <p className="text-sm text-muted-foreground">
                Get your vehicle delivered to your doorstep quickly and safely
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 px-4 bg-muted/50">
        <div className="container mx-auto text-center space-y-6">
          <h2 className="text-3xl font-bold">Ready to Get Started?</h2>
          <p className="text-lg text-muted-foreground max-w-xl mx-auto">
            Join thousands of satisfied customers who found their dream vehicle with BennyCar
          </p>
          <Button size="lg" asChild>
            <Link href="/auth/register">Create Your Account</Link>
          </Button>
        </div>
      </section>
    </div>
  );
}
