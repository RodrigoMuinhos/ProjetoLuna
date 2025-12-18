import { Button } from '../Button';
import { Card } from '../Card';
import { PageContainer } from '../PageContainer';
import { ActionFooter } from '../ActionFooter';
import { Appointment, PaymentMethod } from '../../types';
import { maskName } from '@/lib/nameMask';
import { FlowType, getFlowSteps } from '@/lib/flowSteps';

interface PaymentConfirmationProps {
  appointment: Appointment;
  onSelectMethod: (method: PaymentMethod) => void;
  onBack: () => void;
  flow?: FlowType;
}

export function PaymentConfirmation({
  appointment,
  onSelectMethod,
  onBack,
  flow = 'payment',
}: PaymentConfirmationProps) {
  const handleStartPixPayment = () => {
    onSelectMethod('pix');
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value);
  };
  const steps = getFlowSteps(flow);
  const currentStep = flow === 'checkin' ? 3 : 2;

  return (
    <PageContainer steps={steps} currentStep={currentStep}>
      <div className="w-full flex flex-col items-center gap-8">
        <div className="text-center space-y-3">
          <h2 className="text-3xl md:text-4xl text-[#D3A67F]">
            Confirme o pagamento
          </h2>
        </div>

        <Card className="w-full max-w-2xl">
          <div className="p-8 md:p-12 space-y-8">
            {/* Patient Info */}
            <div className="flex items-center gap-6">
              <div className="w-20 h-20 rounded-full bg-[#D3A67F]/20 flex items-center justify-center flex-shrink-0">
                <svg
                  width="40"
                  height="40"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="#D3A67F"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              </div>
              <div className="text-left">
                <h3 className="text-2xl text-[#D3A67F]">
                  {maskName(appointment.patient.name)}
                </h3>
                <p className="text-[#4A4A4A]/70">
                  {appointment.doctor} - {appointment.specialty}
                </p>
              </div>
            </div>

            {/* Amount */}
            <div className="bg-gradient-to-br from-[#CDDCDC]/30 to-white rounded-2xl p-8 text-center">
              <p className="text-sm text-[#4A4A4A]/70 mb-2">Valor a pagar</p>
              <p className="text-5xl md:text-6xl text-[#D3A67F]">
                {formatCurrency(appointment.amount)}
              </p>
            </div>
          </div>
        </Card>


        {/* Payment Method: Only PIX */}
        <div className="w-full max-w-2xl space-y-4 mb-24">
          <Button
            variant="primary"
            size="xl"
            onClick={handleStartPixPayment}
            className="w-full"
          >
            Ir para pagamento
          </Button>
        </div>
        <ActionFooter
          onBack={onBack}
          onConfirm={handleStartPixPayment}
          confirmLabel="Ir para pagamento"
          showConfirm
        />
      </div>
    </PageContainer>
  );
}
