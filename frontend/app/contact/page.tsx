"use client";

import { useState } from "react";
import { Mail, Phone, MapPin, Send, Clock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

export default function ContactPage() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    subject: "",
    message: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    setIsSubmitting(false);
    setSubmitted(true);
    setFormData({ name: "", email: "", subject: "", message: "" });
    
    setTimeout(() => setSubmitted(false), 5000);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#fafaf8] via-[#f5ede4] to-[#fafaf8]">
      <div className="container mx-auto px-4 py-12">
        {/* Header */}
        <div className="text-center mb-12">
          <h1 className="text-5xl font-bold bg-gradient-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent mb-4">
            Get In Touch
          </h1>
          <p className="text-[#8b7355] text-lg max-w-2xl mx-auto">
            Have questions? We'd love to hear from you. Send us a message and we'll respond as soon as possible.
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 max-w-7xl mx-auto">
          {/* Contact Information Cards */}
          <div className="lg:col-span-1 space-y-6">
            <Card className="border-2 border-[#e8d5c4] shadow-lg hover:shadow-xl transition-shadow">
              <CardContent className="pt-6">
                <div className="flex items-start gap-4">
                  <div className="h-12 w-12 rounded-xl bg-gradient-to-br from-[#c89968] to-[#d4a574] flex items-center justify-center flex-shrink-0">
                    <Mail className="h-6 w-6 text-white" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-[#4a3f35] mb-2">Email Us</h3>
                    <p className="text-sm text-[#8b7355]">support@bennycar.com</p>
                    <p className="text-sm text-[#8b7355]">sales@bennycar.com</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card className="border-2 border-[#e8d5c4] shadow-lg hover:shadow-xl transition-shadow">
              <CardContent className="pt-6">
                <div className="flex items-start gap-4">
                  <div className="h-12 w-12 rounded-xl bg-gradient-to-br from-[#d4a574] to-[#c89968] flex items-center justify-center flex-shrink-0">
                    <Phone className="h-6 w-6 text-white" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-[#4a3f35] mb-2">Call Us</h3>
                    <p className="text-sm text-[#8b7355]">+1 (555) 123-4567</p>
                    <p className="text-sm text-[#8b7355]">Mon-Fri 9am-6pm EST</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card className="border-2 border-[#e8d5c4] shadow-lg hover:shadow-xl transition-shadow">
              <CardContent className="pt-6">
                <div className="flex items-start gap-4">
                  <div className="h-12 w-12 rounded-xl bg-gradient-to-br from-[#8b7355] to-[#c89968] flex items-center justify-center flex-shrink-0">
                    <MapPin className="h-6 w-6 text-white" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-[#4a3f35] mb-2">Visit Us</h3>
                    <p className="text-sm text-[#8b7355]">
                      123 Auto Boulevard<br />
                      New York, NY 10001<br />
                      United States
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card className="border-2 border-[#e8d5c4] shadow-lg hover:shadow-xl transition-shadow">
              <CardContent className="pt-6">
                <div className="flex items-start gap-4">
                  <div className="h-12 w-12 rounded-xl bg-gradient-to-br from-[#c89968] to-[#8b7355] flex items-center justify-center flex-shrink-0">
                    <Clock className="h-6 w-6 text-white" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-[#4a3f35] mb-2">Business Hours</h3>
                    <p className="text-sm text-[#8b7355]">
                      Monday - Friday: 9am - 6pm<br />
                      Saturday: 10am - 4pm<br />
                      Sunday: Closed
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Contact Form */}
          <div className="lg:col-span-2">
            <Card className="border-2 border-[#e8d5c4] shadow-2xl">
              <CardHeader className="pb-6">
                <CardTitle className="text-2xl font-bold text-[#4a3f35]">Send us a Message</CardTitle>
                <CardDescription className="text-[#8b7355]">
                  Fill out the form below and we'll get back to you within 24 hours
                </CardDescription>
              </CardHeader>
              <CardContent>
                {submitted && (
                  <div className="mb-6 p-4 bg-green-50 border-2 border-green-200 rounded-xl">
                    <p className="text-green-700 font-medium">
                      ✓ Message sent successfully! We'll get back to you soon.
                    </p>
                  </div>
                )}
                
                <form onSubmit={handleSubmit} className="space-y-6">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-2">
                      <Label htmlFor="name" className="text-[#4a3f35] font-semibold">
                        Your Name
                      </Label>
                      <Input
                        id="name"
                        name="name"
                        placeholder="John Doe"
                        value={formData.name}
                        onChange={handleChange}
                        required
                        className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] h-12"
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="email" className="text-[#4a3f35] font-semibold">
                        Email Address
                      </Label>
                      <Input
                        id="email"
                        name="email"
                        type="email"
                        placeholder="john@example.com"
                        value={formData.email}
                        onChange={handleChange}
                        required
                        className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] h-12"
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="subject" className="text-[#4a3f35] font-semibold">
                      Subject
                    </Label>
                    <Input
                      id="subject"
                      name="subject"
                      placeholder="How can we help you?"
                      value={formData.subject}
                      onChange={handleChange}
                      required
                      className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] h-12"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="message" className="text-[#4a3f35] font-semibold">
                      Message
                    </Label>
                    <textarea
                      id="message"
                      name="message"
                      rows={6}
                      placeholder="Tell us more about your inquiry..."
                      value={formData.message}
                      onChange={handleChange}
                      required
                      className="w-full rounded-lg border-2 border-[#e8d5c4] bg-white px-4 py-3 text-sm focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 focus:outline-none text-[#4a3f35] placeholder:text-[#8b7355]/50"
                    />
                  </div>

                  <Button
                    type="submit"
                    size="lg"
                    disabled={isSubmitting}
                    className="w-full h-12"
                  >
                    {isSubmitting ? (
                      "Sending..."
                    ) : (
                      <>
                        <Send className="h-5 w-5 mr-2" />
                        Send Message
                      </>
                    )}
                  </Button>
                </form>
              </CardContent>
            </Card>
          </div>
        </div>

        {/* Map Section (Optional) */}
        <div className="mt-12 max-w-7xl mx-auto">
          <Card className="border-2 border-[#e8d5c4] shadow-lg overflow-hidden">
            <div className="h-[400px] bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4] flex items-center justify-center">
              <div className="text-center text-[#8b7355]">
                <MapPin className="h-16 w-16 mx-auto mb-4 text-[#c89968]" />
                <p className="text-lg font-semibold">Map Integration</p>
                <p className="text-sm">Interactive map would be displayed here</p>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}
